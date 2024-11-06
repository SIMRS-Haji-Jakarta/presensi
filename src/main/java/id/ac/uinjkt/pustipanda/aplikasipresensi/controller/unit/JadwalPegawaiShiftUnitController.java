package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JadwalKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JadwalKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/admin-unit/jadwal-pegawai-shift-unit")
public class JadwalPegawaiShiftUnitController {
    @Autowired
    private JadwalKerjaShiftDao jadwalKerjaShiftDao;

    @Autowired
    private JamKerjaShiftDao jamKerjaShiftDao;

    @Autowired
    private CurrentService currentService;

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           Authentication auth,
                           @RequestParam(value = "tanggalCari", required = false) String tanggalCari,
                           @RequestParam(value = "jamKerjaShiftCari", required = false) JamKerjaShift jamKerjaShiftCari,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @PageableDefault(size = 10) Pageable page) {

        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        Page<JadwalKerjaShift> result;

        if (pegawaiCari == null && tanggalCari == null && jamKerjaShiftCari == null) {
            result = jadwalKerjaShiftDao.findAllByAdminUnit(adminUnit, page);
        } else {
            if ((tanggalCari == null || tanggalCari.isEmpty()) && jamKerjaShiftCari == null) {
                result = jadwalKerjaShiftDao.findAllByAdminUnitAndPegawaiOrderByTanggalDesc(adminUnit, pegawaiCari, page);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            } else if (jamKerjaShiftCari == null) {
                result = jadwalKerjaShiftDao.findAllByAdminUnitAndTanggalAndPegawaiOrderByTanggalDesc(adminUnit, LocalDate.parse(tanggalCari), pegawaiCari, page);
                mm.addAttribute("tanggalCari", tanggalCari);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            } else {
                result = jadwalKerjaShiftDao.findAllByAdminUnitAndTanggalAndJamKerjaShiftAndPegawaiOrderByTanggalDesc(adminUnit, LocalDate.parse(tanggalCari), jamKerjaShiftCari, pegawaiCari, page);
                mm.addAttribute("tanggalCari", tanggalCari);
                mm.addAttribute("jamKerjaShiftCari", jamKerjaShiftCari);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            }
        }

        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("jadwalKerjaShift", new JadwalKerjaShift());
        mm.addAttribute("adminUnit", adminUnit);
        mm.addAttribute("data", result);
        return "unit/jadwal-pegawai-shift-unit/list";
    }


    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JadwalKerjaShift o,
                             BindingResult errors,
                             Authentication auth,
                             RedirectAttributes ra) {
        //log.info("JadwalKerjaShift: {}", o);

        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        if (o.getTanggal() == null) {
            ra.addFlashAttribute("message", "Tanggal tidak boleh kosong.");
            return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
        }

        if (o.getJamKerjaShift() == null) {
            ra.addFlashAttribute("message", "Jam kerja shift tidak boleh kosong.");
            return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
        }

        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
        }

        Optional<JadwalKerjaShift> jadwalKerjaShiftCek = jadwalKerjaShiftDao.findByAdminUnitAndPegawaiAndTanggal(adminUnit, o.getPegawai(), o.getTanggal());
        if (jadwalKerjaShiftCek.isPresent()) {
            if (o.getId().equals("")) {
                ra.addFlashAttribute("message", "Pegawai sudah dipetakan untuk tanggal ini.");
                return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
            } else
                o.setId(jadwalKerjaShiftCek.get().getId());
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
        }

        o.setAdminUnit(adminUnit);
        jadwalKerjaShiftDao.save(o);
        return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           Authentication auth,
                           @PathVariable(value = "id", required = false) String id) {
        log.info("jadwal-pegawai-shift: {}", id);
        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftDao.findById(id);
        mm.addAttribute("eData", oJadwalKerjaShift.get());
        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("adminUnit", adminUnit);

        return "unit/jadwal-pegawai-shift-unit/list :: modalEdit";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             Authentication auth,
                             RedirectAttributes ra) {
        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftDao.findByAdminUnitAndId(adminUnit, id);
        if (oJadwalKerjaShift.isPresent()) {
            jadwalKerjaShiftDao.deleteById(id);
        } else {
            ra.addAttribute("message", "Data bukan milik Admin Unit " + adminUnit.getNama());
        }

        return "redirect:/admin-unit/jadwal-pegawai-shift-unit/list";
    }
}
