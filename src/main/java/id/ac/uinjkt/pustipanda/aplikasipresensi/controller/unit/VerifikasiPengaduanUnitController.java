package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengaduanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileStorageService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class VerifikasiPengaduanUnitController {
    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private PengaduanDao pengaduanDao;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    //Dicomment sesuai surat B-5731/B.II/KS.01.3/08/2024 -- 15 Agustus 2024

    /*@PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengaduan")
    public String showVerifikasiPengaduan(@RequestParam(required = false) String id, ModelMap mm,
                                          @RequestParam(value = "mulai", required = false) String mulai,
                                          @RequestParam(value = "selesai", required = false) String selesai,
                                          Authentication auth, @PageableDefault(size = 10) Pageable page) {

        Page<Pengaduan> result;
        Pegawai pegawai = currentService.getCurrentPegawai(auth);

        List<Long> listUnitKerja = pegawaiUtils.generateListUnitKerjaAdminUnit(pegawai);

        if (mulai != null && selesai != null && !mulai.equals("") && !selesai.equals("")) {
            LocalDateTime tglMulai = LocalDate.parse(mulai).atTime(0, 0, 0);
            LocalDateTime tglSelesai = LocalDate.parse(selesai).atTime(23, 59, 59);
            result = pengaduanDao.findAllBySQLTanggalGreaterThanEqualAndTanggalLessThanEqualAndAdminUnitKerjaOrderByTanggalDesc(tglMulai, tglSelesai, listUnitKerja, page);
        } else {
            result = pengaduanDao.findAllBySQLAdminUnitKerjaOrderByTanggalDesc(listUnitKerja, page);
        }

        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("data", result);

        return "unit/verifikasi/pengaduan/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/verifikasi/pengaduan")
    public String updateFormPengaduan(String id, String statusVerifikasi, String catatan, ModelMap mm) {
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);

        if (pengaduan.isPresent()) {
            pengaduan.get().setStatusVerifikasi(new StatusVerifikasi(statusVerifikasi));
            pengaduan.get().setCatatan(catatan);
            pengaduanDao.save(pengaduan.get());
        }

        return "redirect:/admin-unit/verifikasi/pengaduan";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengaduan/view/{id}")
    public String showPengaduan(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException,
            NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException, BucketPolicyTooLargeException {

        log.info("verifikasi untuk pengaduan: {}", id);
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);

        if (!pengaduan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITOLAK.getId())
                && !pengaduan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITERIMA.getId())
                && !pengaduan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITERUSKAN.getId())) {

            //set statusverifikasi => Sedang Diproses
            pengaduan.get().setStatusVerifikasi(AppConstant.STATUS_SEDANG_DIPROSES);
            pengaduanDao.save(pengaduan.get());
        }

        //minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(defaultBucketName).config().build());
        String objectName = pengaduan.get().getUrlBukti();
        //modify urlBukti ke bentuk yang viewable
        pengaduan.get().setUrlBukti(minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(defaultBucketName)
                        .object(objectName)
                        .method(Method.GET)
                        .expiry(2, TimeUnit.HOURS)
                        .build()));

        boolean isFileExists = CommonUtils.isExists(pengaduan.get().getUrlBukti());
        mm.addAttribute("p", pengaduan.get());
        mm.addAttribute("isFileExists", isFileExists);

        return "unit/verifikasi/pengaduan/list :: modalViewPengaduan";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/verifikasi/pengaduan/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengaduan(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
        Pengaduan pengaduan = pengaduanDao.getOne(id);
        String file = pengaduan.getUrlBukti();

        log.info("pengaduan: {}", pengaduan);

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
    @GetMapping("/admin-unit/verifikasi/pengaduan/delete")
    public String deleteDataPengaduan(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);
        if (pengaduan.isPresent()) {
            pengaduanDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/admin-unit/verifikasi/pengaduan";
    }*/
}
