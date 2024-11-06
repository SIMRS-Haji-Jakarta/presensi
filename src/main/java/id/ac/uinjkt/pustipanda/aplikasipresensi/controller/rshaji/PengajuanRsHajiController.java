package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.rshaji;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusVerifikasiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileStorageService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PengajuanService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitKerjaPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Controller
@Slf4j
public class PengajuanRsHajiController {
    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private StatusVerifikasiDao statusVerifikasiDao;

    @Autowired
    private PengajuanService pengajuanService;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private UnitKerjaPresensiService unitKerjaPresensiService;

    @Autowired
    private FileStorageService fileStorageService;

    // Pengajuan dari pegawai
    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/rshaji/verifikasi/pengajuan")
    public String showVerifikasiPengajuan(ModelMap mm,
                                          @RequestParam(value = "nama", required = false) String nama,
                                          @RequestParam(value = "mulai", required = false) String mulai,
                                          @RequestParam(value = "selesai", required = false) String selesai,
                                          @RequestParam(value = "status", required = false) StatusVerifikasi status,
                                          @PageableDefault(size = 10) Pageable page) {

        Optional<UnitKerjaPresensi> unitRsHaji = unitKerjaPresensiService.getById(UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI); // Rumah Sakit Haji
        Page<Pengajuan> result = pengajuanService.getFilterBySearchParamAndPage(nama, null, mulai, selesai, status, unitRsHaji.get(), page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("status", status);
        mm.addAttribute("mulai", mulai);
        mm.addAttribute("selesai", selesai);
        mm.addAttribute("listStatus", statusVerifikasiDao.findAllByFilter());
        mm.addAttribute("data", result);

        return "rshaji/verifikasi/pengajuan/list";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @PostMapping("/rshaji/verifikasi/pengajuan")
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

        return "redirect:/rshaji/verifikasi/pengajuan";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/rshaji/verifikasi/pengajuan/view/{id}")
    public String showPengajuan(ModelMap mm, @PathVariable(value = "id", required = false) String id) throws IOException,
            InvalidKeyException, NoSuchAlgorithmException {

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

        return "rshaji/verifikasi/pengajuan/list :: modalViewPengajuan";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/rshaji/verifikasi/pengajuan/berkas/{id}")
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

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/rshaji/verifikasi/pengajuan/delete")
    public String deletePengajuan(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent()) {
            pengajuanDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/rshaji/verifikasi/pengajuan";
    }
}
