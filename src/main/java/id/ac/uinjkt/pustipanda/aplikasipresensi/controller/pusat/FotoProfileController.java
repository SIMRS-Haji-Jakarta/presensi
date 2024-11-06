package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.FotoProfileDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.KategoriPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileProfileService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FotoProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/pusat/foto-profile")
public class FotoProfileController {

    @Autowired
    private FotoProfileDao fotoProfileDao;

    @Autowired
    private FotoProfileService fotoProfileService;

    @Autowired
    private FileProfileService fileProfileService;

    @Autowired
    private KategoriPegawaiDao kategoriPegawaiDao;

    @Autowired
    private PegawaiDao pegawaiDao;

    @Autowired
    private UnitDao unitDao;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @RequestParam(value = "status", required = false, defaultValue = "false") String statusName,
                           @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai,
                           @PageableDefault(size = 10) Pageable page) {

        Page<FotoProfileDto> result = new PageImpl<>(new LinkedList<>());

        Boolean status = null;
        if (statusName.equals("true"))
            status = true;
        else if (statusName.equals("false"))
            status = false;

        if (pegawaiCari != null && idKategoriPegawai == null) {
            if (status != null)
                result = fotoProfileService.getAllByPegawaiAndStatusOrderByCreatedAtDesc(pegawaiCari, status, page);
            else
                result = fotoProfileService.getAllPegawaiBelumUploadByPegawaiOrderByCreatedAtDesc(pegawaiCari, page);
            mm.addAttribute("pegawaiCari", pegawaiCari);
        }

        if (pegawaiCari != null && idKategoriPegawai != null) {
            if (status != null)
                result = fotoProfileService.getAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, pegawaiCari.getId(), status, page);
            else
                result = fotoProfileService.getAllPegawaiBelumUploadByKategoriPegawaiAndPegawaiIdOrderByCreatedAtDesc(idKategoriPegawai, pegawaiCari.getId(), page);
            mm.addAttribute("pegawaiCari", pegawaiCari);
        }

        if (pegawaiCari == null && idKategoriPegawai != null) {
            if (status != null)
                result = fotoProfileService.getAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, status, page);
            else
                result = fotoProfileService.getAllPegawaiBelumUploadByKategoriPegawaiAndOrderByCreatedAtDesc(idKategoriPegawai, page);
        }

        if (pegawaiCari == null && idKategoriPegawai == null) {
            if (status != null)
                result = fotoProfileService.getAllByStatusOrderByCreatedAtDesc(status, page);
            else
                result = fotoProfileService.getAllPegawaiBelumUploadOrderByCreatedAtDesc(page);
        }

        mm.addAttribute("status", statusName);
        mm.addAttribute("idKategoriPegawai", idKategoriPegawai);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("data", result);

        return "pusat/foto-profile/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/view/{id}")
    public String showInfoFotoProfile(ModelMap mm,
                                      @PathVariable(value = "id", required = false) String id) {

        Optional<FotoProfile> result = fotoProfileDao.findById(id);
        if (result.isPresent())
            result.get().setUrlBukti(fotoProfileService.getViewableUrl(result.get(), ""));

        mm.addAttribute("fotoProfile", result.get());

        return "pusat/foto-profile/list :: validateFotoProfile";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @ResponseBody
    @GetMapping("/validate")
    public Map<String, Object> validateFoto(@RequestParam(value = "id", required = false) String id) {
        Map<String, Object> hasil = new HashMap<>();

        hasil.put("status", true);
        hasil.put("message", "error");

        Optional<FotoProfile> fotoProfile = fotoProfileDao.findById(id);
        if (fotoProfile.isPresent()) {
            fotoProfile.get().setStatus(true);
            fotoProfileService.saveFotoProfile(fotoProfile.get());

            log.info("Validate foto berhasil untuk pegawai {}", fotoProfile.get().getPegawai().getId());
            hasil.put("status", true);
            hasil.put("message", "berhasil");
        }

        return hasil;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/foto/{id}")
    public ResponseEntity<ByteArrayResource> downloadFotoProfile(@PathVariable("id") String id) throws IOException {
        Optional<FotoProfile> fotoProfile = fotoProfileDao.findById(id);

        String file = "";
        String nama = "";
        if (fotoProfile.isPresent() && fotoProfile.get().getStatus()) {
            file = fotoProfile.get().getUrlBukti();
            nama = fotoProfile.get().getPegawai().getNik() + "_" + FotoProfileService.PROFILE + "." + FotoProfileService.IMG_EXT;
        }

        log.info("admin akses file foto: {}", fotoProfile);

        byte[] data = fotoProfileService.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + nama + "\"")
                .body(resource);
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) throws IOException {
        Optional<FotoProfile> fotoProfile = fotoProfileDao.findById(id);
        if (fotoProfile.isPresent() && !fotoProfile.get().getStatus()) {
            fileProfileService.removeFile(fotoProfile.get());
            fotoProfileDao.deleteById(id);
            log.info("admin hapus file foto: {}", fotoProfile);
        } else {
            ra.addFlashAttribute("message", "Terjadi kesalahan");
        }

        return "redirect:/pusat/foto-profile/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/reset")
    public String resetValidasiFoto(@RequestParam(value = "id", required = false) String id) {

        Optional<FotoProfile> fotoProfile = fotoProfileDao.findById(id);
        if (fotoProfile.isPresent() && fotoProfile.get().getStatus()) {
            fotoProfile.get().setStatus(false);
            fotoProfileService.saveFotoProfile(fotoProfile.get());

            log.info("Reset validasi foto berhasil untuk pegawai {}", fotoProfile.get().getPegawai().getId());
        }

        return "redirect:/pusat/foto-profile/list";
    }

    /*report*/

    @PreAuthorize("hasAnyAuthority('am','pegpst','puskom')")
    @GetMapping("/rekapfotoprofile")
    public String showRekapFoto(ModelMap mm, @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai) {

        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));

        return "pusat/foto-profile/rekap-foto-profile";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','puskom')")
    @PostMapping("/rekapfotoprofile-print-all")
    public String printRekapFoto(ModelMap mm, @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai,
                                 @RequestParam(value = "unit") Long unit) {

        LocalDate tglSekarang = LocalDate.now();

        List<FotoProfileDto> resultFotoProfile = new LinkedList<>();
        if (idKategoriPegawai != null && unit == null)
            resultFotoProfile = fotoProfileDao.fotoProfilePegawaiByStatusAndKategoriPegawai(idKategoriPegawai, Boolean.TRUE);
        if (idKategoriPegawai == null && unit != null)
            resultFotoProfile = fotoProfileDao.fotoProfilePegawaiByStatusAndUnit(Boolean.TRUE, unit);
        if (idKategoriPegawai != null && unit != null)
            resultFotoProfile = fotoProfileDao.fotoProfilePegawaiByStatusAndKategoriPegawaiAndUnit(idKategoriPegawai, unit, Boolean.TRUE);
        if (idKategoriPegawai == null && unit == null)
            resultFotoProfile = fotoProfileDao.fotoProfilePegawaiByStatus(Boolean.TRUE);

        if (resultFotoProfile.size() != 0) {
            int i = 0;
            for (FotoProfileDto profileDto : resultFotoProfile) {
                Optional<Pegawai> pegawai = pegawaiDao.findById(profileDto.getIdPegawai());
                if (pegawai.isPresent())
                    resultFotoProfile.get(i).setNama(Pegawai.toStringNama(pegawai.get()));
                try {
                    FotoProfile fotoProfile = new FotoProfile();
                    fotoProfile.setUrlBukti(profileDto.getUrlBerkas());

                    resultFotoProfile.get(i).setUrlBerkas(fotoProfileService.getViewableUrl(fotoProfile, ""));
                } catch (Exception e) {
                    log.error("Error resolve url :", e);
                }
                i++;
            }
        }

        Unit oUnit = null;
        if (unit != null)
            oUnit = unitDao.findById(unit).get();

        mm.addAttribute("namaUnit", oUnit != null ? oUnit.getNama() : "Semua");
        mm.addAttribute("data", resultFotoProfile);
        mm.addAttribute("tglSekarang", tglSekarang);

        return "pusat/foto-profile/rekap-foto-profile-print-all";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/rekapbelumuploadfoto")
    public String showRekapBelumUploadFoto(ModelMap mm, @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai) {

        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));

        return "pusat/foto-profile/rekap-belum-upload-foto";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/rekapbelumuploadfoto-print-all")
    public String printRekapBelumUploadFoto(ModelMap mm, @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai,
                                            @RequestParam(value = "unit") Long unit) {

        LocalDate tglSekarang = LocalDate.now();

        List<FotoProfileDto> resultFotoProfile = new LinkedList<>();
        if (idKategoriPegawai != null && unit == null)
            resultFotoProfile = fotoProfileDao.fotoProfileBelumUploadByKategoriPegawai(idKategoriPegawai);
        if (idKategoriPegawai == null && unit != null)
            resultFotoProfile = fotoProfileDao.fotoProfileBelumUploadByUnit(unit);
        if (idKategoriPegawai != null && unit != null)
            resultFotoProfile = fotoProfileDao.fotoProfileBelumUploadByKategoriPegawaiAndUnit(idKategoriPegawai, unit);
        if (idKategoriPegawai == null && unit == null)
            resultFotoProfile = fotoProfileDao.fotoProfileBelumUpload();

        if (resultFotoProfile.size() != 0) {
            int i = 0;
            for (FotoProfileDto profileDto : resultFotoProfile) {
                Optional<Pegawai> pegawai = pegawaiDao.findById(profileDto.getIdPegawai());
                if (pegawai.isPresent())
                    resultFotoProfile.get(i).setNama(Pegawai.toStringNama(pegawai.get()));
                i++;
            }
        }

        Unit oUnit = null;
        if (unit != null)
            oUnit = unitDao.findById(unit).get();

        mm.addAttribute("namaUnit", oUnit != null ? oUnit.getNama() : "Semua");
        mm.addAttribute("data", resultFotoProfile);
        mm.addAttribute("tglSekarang", tglSekarang);

        return "pusat/foto-profile/rekap-belum-upload-foto-print-all";
    }

}
