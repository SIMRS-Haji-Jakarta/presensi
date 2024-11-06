package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengaduanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusVerifikasiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengaduan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileStorageService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PengaduanService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@Slf4j
public class PengaduanController {
    @Autowired
    private PengaduanDao pengaduanDao;

    @Autowired
    private PengaduanService pengaduanService;

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private StatusVerifikasiDao statusVerifikasiDao;

    @Autowired
    private FileStorageService fileStorageService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan-admin-unit")
    public String showVerifikasiPengaduanAM(@RequestParam(required = false) String id, ModelMap mm,
                                            @RequestParam(value = "mulai", required = false) String mulai,
                                            @RequestParam(value = "selesai", required = false) String selesai,
                                            @PageableDefault(size = 10) Pageable page) {

        Page<Pengaduan> result;

        if (mulai != null && selesai != null && !mulai.equals("") && !selesai.equals("")) {
            LocalDateTime tglMulai = LocalDate.parse(mulai).atTime(0, 0, 0);
            LocalDateTime tglSelesai = LocalDate.parse(selesai).atTime(23, 59, 59);
            result = pengaduanDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualAndStatusVerifikasiOrderByTanggalDesc(tglMulai, tglSelesai, AppConstant.STATUS_DITERUSKAN, page);
        } else {
            result = pengaduanDao.findAllByStatusVerifikasiOrderByTanggalDesc(AppConstant.STATUS_DITERUSKAN, page);
        }

        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("data", result);

        return "pusat/verifikasi/pengaduan/pengaduan-admin-unit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/pusat/verifikasi/pengaduan-admin-unit")
    public String updateFormPengaduanAM(String id, String statusVerifikasi, String catatan, ModelMap mm) {
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);

        if (pengaduan.isPresent()) {
            pengaduan.get().setStatusVerifikasi(new StatusVerifikasi(statusVerifikasi));
            pengaduan.get().setCatatan(catatan);
            pengaduanDao.save(pengaduan.get());
        }

        return "redirect:/pusat/verifikasi/pengaduan/pengaduan-admin-unit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan-admin-unit/view/{id}")
    public String showPengaduanAM(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, NoSuchAlgorithmException {

        log.info("verifikasi untuk pengaduan: {}", id);
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);

        //minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(defaultBucketName).config().build());
//        String objectName = pengaduan.get().getUrlBukti();
        //modify urlBukti ke bentuk yang viewable
//        pengaduan.get().setUrlBukti(fileStorageService.getPresignedObjectUrl(
//                GetPresignedObjectUrlArgs.builder()
//                        .bucket(defaultBucketName)
//                        .object(objectName)
//                        .method(Method.GET)
//                        .expiry(2, TimeUnit.HOURS)
//                        .build()));

        boolean isFileExists = CommonUtils.isExists(pengaduan.get().getUrlBukti());
        mm.addAttribute("p", pengaduan.get());
        mm.addAttribute("isFileExists", isFileExists);

        return "pusat/verifikasi/pengaduan/pengaduan-admin-unit :: modalViewPengaduan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan-admin-unit/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengaduanAM(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        Pengaduan pengaduan = pengaduanDao.getOne(id);
        String file = pengaduan.getUrlBukti();

        log.info("admin pusat pengaduan: {}", pengaduan);

        byte[] data = fileStorageService.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);
    }


    // Pengaduan dari pegawai
    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan")
    public String showVerifikasiPengaduan(ModelMap mm,
                                          @RequestParam(value = "nama", required = false) String nama,
                                          @RequestParam(value = "mulai", required = false) String mulai,
                                          @RequestParam(value = "selesai", required = false) String selesai,
                                          @RequestParam(value = "unit", required = false) Unit unit,
                                          @RequestParam(value = "status", required = false) StatusVerifikasi status,
                                          @PageableDefault(size = 10) Pageable page) {

        Page<Pengaduan> result = pengaduanService.getFilterBySearchParamAndPage(nama, unit, mulai, selesai, status, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("unit", unit);
        mm.addAttribute("status", status);
        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));
        mm.addAttribute("listStatus", statusVerifikasiDao.findAllByFilter());
        mm.addAttribute("data", result);

        return "pusat/verifikasi/pengaduan/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/pusat/verifikasi/pengaduan")
    public String updateFormPengaduan(String id, String statusVerifikasi, String catatan) {
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);

        if (pengaduan.isPresent()) {
            pengaduan.get().setStatusVerifikasi(new StatusVerifikasi(statusVerifikasi));
            pengaduan.get().setCatatan(catatan);
            pengaduanDao.save(pengaduan.get());
        }

        return "redirect:/pusat/verifikasi/pengaduan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan/view/{id}")
    public String showPengaduan(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, NoSuchAlgorithmException {

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
//        pengaduan.get().setUrlBukti(fileStorageService.getPresignedObjectUrl(
//                GetPresignedObjectUrlArgs.builder()
//                        .bucket(defaultBucketName)
//                        .object(objectName)
//                        .method(Method.GET)
//                        .expiry(2, TimeUnit.HOURS)
//                        .build()));

        boolean isFileExists = CommonUtils.isExists(pengaduan.get().getUrlBukti());
        mm.addAttribute("p", pengaduan.get());
        mm.addAttribute("isFileExists", isFileExists);

        return "pusat/verifikasi/pengaduan/list :: modalViewPengaduan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengaduan(@PathVariable("id") String id) throws IOException {
        Pengaduan pengaduan = pengaduanDao.getOne(id);
        String file = pengaduan.getUrlBukti();

        log.info("pengaduan: {}", pengaduan);

        byte[] data = fileStorageService.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);

    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengaduan/delete")
    public String deleteDataPengaduan(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengaduan> pengaduan = pengaduanDao.findById(id);
        if (pengaduan.isPresent()) {
            pengaduanDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/verifikasi/pengaduan";
    }
}
