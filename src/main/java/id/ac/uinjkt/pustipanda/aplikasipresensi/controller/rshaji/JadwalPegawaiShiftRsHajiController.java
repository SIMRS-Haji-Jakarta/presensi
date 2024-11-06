package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.rshaji;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JadwalKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.JadwalKerjaShiftService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitKerjaPresensiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/rshaji/jadwal-pegawai-shift")
public class JadwalPegawaiShiftRsHajiController {
    @Autowired
    private JadwalKerjaShiftService jadwalKerjaShiftService;

    @Autowired
    private UnitKerjaPresensiService unitKerjaPresensiService;

    @Autowired
    private JamKerjaShiftDao jamKerjaShiftDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "tanggalCari", required = false) String tanggalCari,
                           @RequestParam(value = "jamKerjaShiftCari", required = false) JamKerjaShift jamKerjaShiftCari,
                           @RequestParam(value = "nama", required = false) String nama,
                           @PageableDefault(size = 10) Pageable page) {

        Optional<UnitKerjaPresensi> unitRsHaji = unitKerjaPresensiService.getById(UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI); // Rumah Sakit Haji
        Page<JadwalKerjaShift> result = jadwalKerjaShiftService.getFilterBySearchParamAndPage(nama, tanggalCari == null ? null : LocalDate.parse(tanggalCari),
                jamKerjaShiftCari, unitRsHaji.get(), page);

        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("jadwalKerjaShift", new JadwalKerjaShift());
        mm.addAttribute("idUnitKerja", UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI);
        mm.addAttribute("nama", nama);
        mm.addAttribute("tanggalCari", tanggalCari);
        mm.addAttribute("jamKerjaShiftCari", jamKerjaShiftCari);
        mm.addAttribute("data", result);

        return "rshaji/jadwal-pegawai-shift/list";
    }


    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JadwalKerjaShift o,
                             BindingResult errors,
                             RedirectAttributes ra) {
        //log.info("JadwalKerjaShift: {}", o);

//        if (o.getAdminUnit() == null) {
//            ra.addFlashAttribute("message", "Admin unit tidak boleh kosong.");
//            return "redirect:/rshaji/jadwal-pegawai-shift/list";
//        }


        if (o.getTanggal() == null) {
            ra.addFlashAttribute("message", "Tanggal tidak boleh kosong.");
            return "redirect:/rshaji/jadwal-pegawai-shift/list";
        }

        if (o.getJamKerjaShift() == null) {
            ra.addFlashAttribute("message", "Jam kerja shift tidak boleh kosong.");
            return "redirect:/rshaji/jadwal-pegawai-shift/list";
        }

        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/rshaji/jadwal-pegawai-shift/list";
        }

        Optional<JadwalKerjaShift> jadwalKerjaShiftCek = jadwalKerjaShiftService.findByAdminUnitAndPegawaiAndTanggal(o.getAdminUnit(), o.getPegawai(), o.getTanggal());
        if (jadwalKerjaShiftCek.isPresent()) {
            if (o.getId().equals("")) {
                ra.addFlashAttribute("message", "Pegawai sudah dipetakan untuk tanggal ini.");
                return "redirect:/rshaji/jadwal-pegawai-shift/list";
            } else
                o.setId(jadwalKerjaShiftCek.get().getId());
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/rshaji/jadwal-pegawai-shift/list";
        }

        jadwalKerjaShiftService.save(o);
        return "redirect:/rshaji/jadwal-pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           @PathVariable(value = "id", required = false) String id) {
        log.info("jadwal-pegawai-shift: {}", id);
        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftService.findById(id);
        mm.addAttribute("eData", oJadwalKerjaShift.get());
        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));
        mm.addAttribute("idUnitKerja", UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI);

        return "rshaji/jadwal-pegawai-shift/list :: modalEdit";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             ModelMap mm,
                             RedirectAttributes ra) {
        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftService.findById(id);
        if (oJadwalKerjaShift.isPresent()) {
            jadwalKerjaShiftService.delete(oJadwalKerjaShift.get());
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/rshaji/jadwal-pegawai-shift/list";
    }
}
