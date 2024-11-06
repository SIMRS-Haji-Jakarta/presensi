package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileStorageService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PengajuanService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class VerifikasiPengajuanUnitController {
    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @Autowired
    private PengajuanService pengajuanService;

    //Dicomment sesuai surat B-5731/B.II/KS.01.3/08/2024 -- 15 Agustus 2024

    /*@PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengajuan")
    public String showVerifikasiPengajuan(@RequestParam(required = false) String id, ModelMap mm,
                                          @RequestParam(value = "mulai", required = false) String mulai,
                                          @RequestParam(value = "selesai", required = false) String selesai,
                                          Authentication auth, @PageableDefault(size = 10) Pageable page) {

        Page<Pengajuan> result;
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        //log.info("pegawai: {}", pegawai);

        List<Long> listUnitKerja = pegawaiUtils.generateListUnitKerjaAdminUnit(pegawai);

        if (mulai != null && selesai != null && !mulai.equals("") && !selesai.equals("")) {
            LocalDateTime tglMulai = LocalDate.parse(mulai).atTime(0, 0, 0);
            LocalDateTime tglSelesai = LocalDate.parse(selesai).atTime(23, 59, 59);

            result = pengajuanDao.findAllBySQLTanggalGreaterThanEqualAndTanggalLessThanEqualAndAdminUnitKerjaOrderByTanggalDesc(tglMulai, tglSelesai, listUnitKerja, page);
        } else {
            result = pengajuanDao.findAllBySQLAdminUnitKerjaOrderByTanggalDesc(listUnitKerja, page);
        }

        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("data", result);

        return "unit/verifikasi/pengajuan/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/verifikasi/pengajuan")
    public String updateForm(String id, String statusVerifikasi, String catatan, ModelMap mm) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);

        if (pengajuan.isPresent()) {
            pengajuan.get().setStatusVerifikasi(new StatusVerifikasi(statusVerifikasi));
            pengajuan.get().setCatatan(catatan);
            pengajuanDao.save(pengajuan.get());

            //proses pengajuan langsung
            if (pengajuan.get().getStatusVerifikasi().equals(AppConstant.STATUS_DITERIMA)) {
                pengajuanService.prosesPengajuan(pengajuan.get());
            }
        }

        return "redirect:/admin-unit/verifikasi/pengajuan";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengajuan/view/{id}")
    public String showPengajuan(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException,
            NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException, BucketPolicyTooLargeException {

        log.info("verifikasi untuk pengajuan: {}", id);
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);

        if (!pengajuan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITOLAK.getId())
                && !pengajuan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITERIMA.getId())) {

            //set statusverifikasi => Sedang Diproses
            pengajuan.get().setStatusVerifikasi(AppConstant.STATUS_SEDANG_DIPROSES);
            pengajuanDao.save(pengajuan.get());
        }

        if (!pengajuan.get().getIsUrl()) {
            //minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(defaultBucketName).config().build());
            String objectName = pengajuan.get().getUrlBukti();
            //modify urlBukti ke bentuk yang viewable
            pengajuan.get().setUrlBukti(minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(defaultBucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(2, TimeUnit.HOURS)
                            .build()));
        }

        boolean isFileExists = CommonUtils.isExists(pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
        String mimeType = "";
        if (isFileExists) {
            mimeType = currentService.getMimeType(pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
        }
        mm.addAttribute("mimeType", mimeType);
        mm.addAttribute("p", pengajuan.get());
        mm.addAttribute("isFileExists", isFileExists);

        return "unit/verifikasi/pengajuan/list :: modalViewPengajuan";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengajuan/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengajuan(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
        Pengajuan pengajuan = pengajuanDao.getOne(id);
        String file = pengajuan.getUrlBukti();

        log.info("pengajuan: {}", pengajuan);

        byte[] data = minioAdapter.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);

    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengajuan/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent()) {
            pengajuanDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/admin-unit/verifikasi/pengajuan";
    }*/
}
