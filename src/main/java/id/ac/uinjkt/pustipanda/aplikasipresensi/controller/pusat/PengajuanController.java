package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusVerifikasiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FilePengajuanService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileStorageService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PengajuanService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@Slf4j
public class PengajuanController {
    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private StatusVerifikasiDao statusVerifikasiDao;

    @Autowired
    private StatusPresensiDao statusPresensiDao;

    @Autowired
    private PengajuanService pengajuanService;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FilePengajuanService filePengajuanService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/reset")
    public String showVerifikasiPengajuanAM(@RequestParam(required = false) String id, ModelMap mm,
                                            @RequestParam(value = "tanggal", required = false) String tanggal,
                                            @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                            @RequestParam(value = "statusVerifikasi", required = false) String idStatusVerifikasi,
                                            @PageableDefault(size = 10) Pageable page) {

        Page<Pengajuan> result = null;
        StatusVerifikasi statusVerifikasi = null;

        if (idStatusVerifikasi != null && !idStatusVerifikasi.isEmpty())
            statusVerifikasi = statusVerifikasiDao.getOne(idStatusVerifikasi);

        if (id != null) {
            //reset pengajuan
            Optional<Pengajuan> p = pengajuanDao.findById(id);
            if (p.isPresent()) {
                pengajuanService.resetPengajuan(p.get().getId());

                return "redirect:/pusat/verifikasi/pengajuan/reset?tanggal=" + p.get().getTanggal().toLocalDate() + "&pegawaiCari="
                        + p.get().getPegawai().getId() + "&statusVerifikasi=" + p.get().getStatusVerifikasi().getId();
            }
        }

        if ((tanggal != null && !tanggal.isEmpty()) && pegawaiCari == null) {
            LocalDateTime tglBatasBawah = LocalDate.parse(tanggal).atTime(0, 0, 0);
            LocalDateTime tglBatasAtas = LocalDate.parse(tanggal).atTime(23, 59, 59);

            if (statusVerifikasi != null)
                result = pengajuanDao.findAllByTanggalGreaterThanAndTanggalLessThanAndStatusVerifikasiOrderByTanggalDesc(tglBatasBawah, tglBatasAtas, statusVerifikasi, page);
            else
                result = pengajuanDao.findAllByTanggalGreaterThanAndTanggalLessThanOrderByTanggalDesc(tglBatasBawah, tglBatasAtas, page);
        }

        if ((tanggal != null && !tanggal.isEmpty()) && pegawaiCari != null) {
            LocalDateTime tglBatasBawah = LocalDate.parse(tanggal).atTime(0, 0, 0);
            LocalDateTime tglBatasAtas = LocalDate.parse(tanggal).atTime(23, 59, 59);

            if (statusVerifikasi != null)
                result = pengajuanDao.findAllByTanggalGreaterThanAndTanggalLessThanAndPegawaiAndStatusVerifikasiOrderByTanggalDesc(tglBatasBawah, tglBatasAtas, pegawaiCari, statusVerifikasi, page);
            else
                result = pengajuanDao.findAllByTanggalGreaterThanAndTanggalLessThanAndPegawaiOrderByTanggalDesc(tglBatasBawah, tglBatasAtas, pegawaiCari, page);
        }

        if ((tanggal == null || tanggal.isEmpty()) && pegawaiCari != null) {
            if (statusVerifikasi != null)
                result = pengajuanDao.findAllByPegawaiAndStatusVerifikasiOrderByTanggalDesc(pegawaiCari, statusVerifikasi, page);
            else
                result = pengajuanDao.findAllByPegawaiOrderByTanggalDesc(pegawaiCari, page);
        }

        if ((tanggal == null || tanggal.isEmpty()) && pegawaiCari == null) {
            if (statusVerifikasi != null)
                result = pengajuanDao.findAllByStatusVerifikasiOrderByTanggalDesc(statusVerifikasi, page);
            else
                result = pengajuanDao.findAllByOrderByTanggalDesc(page);
        }

        mm.addAttribute("tanggal", tanggal);
        mm.addAttribute("data", result);
        mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());

        return "pusat/verifikasi/pengajuan/reset";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/reset/view/{id}")
    public String showList(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, NoSuchAlgorithmException {

        log.info("verifikasi untuk pengajuan (pusat): {}", id);
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);

        if (pengajuan.isPresent()) {
            boolean isFileExists = CommonUtils.isExists(pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
            String mimeType = "";
            if (isFileExists) {
                mimeType = currentService.getMimeType(pengajuan.get().getIsUrl(),
                        pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
                if (!pengajuan.get().getIsUrl()) {
                    pengajuan.get().setUrlBukti(pengajuanService.getViewableUrl(pengajuan.get(), ""));
                }
            }

            mm.addAttribute("isFileExists", isFileExists);
            mm.addAttribute("mimeType", mimeType);
            mm.addAttribute("p", pengajuan.get());
        }

        mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());
        mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());

        return "pusat/verifikasi/pengajuan/reset :: modalViewPengajuan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/pusat/verifikasi/pengajuan/reset")
    public String updateForm(@ModelAttribute @Valid Pengajuan o, BindingResult errors, ModelMap mm) {

        if (o.getTanggalMulai() == null || o.getTanggalMulai().isAfter(o.getTanggalSelesai())) {
            errors.rejectValue("tanggalMulai", "tanggalMulai", "Periode mulai tidak boleh kosong atau tidak valid");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
            mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());
        }

        if (o.getTanggalSelesai() == null || o.getTanggalSelesai().isBefore(o.getTanggalMulai())) {
            errors.rejectValue("tanggalSelesai", "tanggalSelesai", "Periode selesai tidak boleh kosong atau tidak valid");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
            mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());
        }

        if (o.getStatusPresensi() == null
                || o.getStatusPresensi().getId().equals(AppConstant.STATUS_HADIR.getId())
                || o.getStatusPresensi().getId().equals(AppConstant.STATUS_ALFA.getId())) {
            errors.rejectValue("statusPresensi", "statusPresensi", "Status presensi tidak boleh kosong");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
            mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());
        }

        if (errors.hasErrors()) {
            mm.addAttribute("listStatusVerifikasi", statusVerifikasiDao.findAllByFilter());
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());

            return "pusat/verifikasi/pengajuan/reset :: modalViewPengajuan";
        } else {
            if (o.getId() != null) {
                pengajuanService.updatePengajuan(o);
                //proses pengajuan langsung
                if (o.getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITERIMA.getId())) {
                    pengajuanService.prosesPengajuan(o);
                }
            }
            return "redirect:/pusat/verifikasi/pengajuan/reset?tanggal=" + o.getTanggal().toLocalDate() + "&pegawaiCari="
                    + o.getPegawai().getId() + "&statusVerifikasi=" + o.getStatusVerifikasi().getId();
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/reset/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengajuan(@PathVariable("id") String id) throws IOException {
        return pengajuanService.getFilePengajuan(id);
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/reset/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent() && !pengajuan.get().getStatusProses()) {
            pengajuanService.deletePengajuanById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/verifikasi/pengajuan/reset";
    }

    // Pengajuan dari pegawai
    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan")
    public String showVerifikasiPengajuan(ModelMap mm,
                                          @RequestParam(value = "nama", required = false) String nama,
                                          @RequestParam(value = "mulai", required = false) String mulai,
                                          @RequestParam(value = "selesai", required = false) String selesai,
                                          @RequestParam(value = "unit", required = false) Unit unit,
                                          @RequestParam(value = "status", required = false) StatusVerifikasi status,
                                          @PageableDefault(size = 10) Pageable page) {

        Page<Pengajuan> result = pengajuanService.getFilterBySearchParamAndPage(nama, unit, mulai, selesai, status, null, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("unit", unit);
        mm.addAttribute("status", status);
        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));
        mm.addAttribute("listStatus", statusVerifikasiDao.findAllByFilter());
        mm.addAttribute("data", result);

        return "pusat/verifikasi/pengajuan/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/pusat/verifikasi/pengajuan")
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

        return "redirect:/pusat/verifikasi/pengajuan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/view/{id}")
    public String showPengajuan(ModelMap mm, @PathVariable(value = "id", required = false) String id) {

        log.info("verifikasi untuk pengajuan: {}", id);
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);

        if (!pengajuan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITOLAK.getId())
                && !pengajuan.get().getStatusVerifikasi().getId().equals(AppConstant.STATUS_DITERIMA.getId())) {

            //set statusverifikasi => Sedang Diproses
            pengajuan.get().setStatusVerifikasi(AppConstant.STATUS_SEDANG_DIPROSES);
            pengajuanDao.save(pengajuan.get());
        }

        boolean isFileExists = CommonUtils.isExists(pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
        String mimeType = "";
        if (isFileExists) {
            mimeType = currentService.getMimeType(pengajuan.get().getIsUrl(),
                    pengajuan.get().getIsUrl() ? pengajuan.get().getUrlShare() : pengajuan.get().getUrlBukti());
            if (!pengajuan.get().getIsUrl()) {
                pengajuan.get().setUrlBukti(pengajuanService.getViewableUrl(pengajuan.get(), ""));
            }
        }
        mm.addAttribute("mimeType", mimeType);
        mm.addAttribute("p", pengajuan.get());
        mm.addAttribute("isFileExists", isFileExists);

        return "pusat/verifikasi/pengajuan/list :: modalViewPengajuan";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/pusat/verifikasi/pengajuan/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengajuanFromPegawai(@PathVariable("id") String id) throws IOException {
        Pengajuan pengajuan = pengajuanDao.getOne(id);
        String file = pengajuan.getUrlBukti();

        log.info("pengajuan: {}", pengajuan);

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
    @GetMapping("/pusat/verifikasi/pengajuan/delete")
    public String deletePengajuan(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent()) {
            filePengajuanService.removeFile(pengajuan.get());
            pengajuanDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/verifikasi/pengajuan";
    }
}
