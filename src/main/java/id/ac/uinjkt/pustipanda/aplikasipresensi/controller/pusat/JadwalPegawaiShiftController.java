package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JadwalKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JadwalKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
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
@RequestMapping("/pusat/jadwal-pegawai-shift")
public class JadwalPegawaiShiftController {
    @Autowired
    private JadwalKerjaShiftDao jadwalKerjaShiftDao;

    @Autowired
    private JamKerjaShiftDao jamKerjaShiftDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "tanggalCari", required = false) String tanggalCari,
                           @RequestParam(value = "jamKerjaShiftCari", required = false) JamKerjaShift jamKerjaShiftCari,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @RequestParam(value = "adminUnitCari", required = false) AdminUnit adminUnitCari,
                           @PageableDefault(size = 10) Pageable page) {

        Page<JadwalKerjaShift> result;

        if (adminUnitCari == null && pegawaiCari == null && tanggalCari == null && jamKerjaShiftCari == null) {
            result = jadwalKerjaShiftDao.findAll(page);
        } else {
            if ((tanggalCari == null || tanggalCari.isEmpty()) && jamKerjaShiftCari == null) {
                result = jadwalKerjaShiftDao.findAllByPegawaiOrderByTanggalDesc(pegawaiCari, page);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            } else if (jamKerjaShiftCari == null) {
                result = jadwalKerjaShiftDao.findAllByTanggalAndPegawaiOrderByTanggalDesc(LocalDate.parse(tanggalCari), pegawaiCari, page);
                mm.addAttribute("tanggalCari", tanggalCari);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            } else {
                result = jadwalKerjaShiftDao.findAllByTanggalAndJamKerjaShiftAndPegawaiOrderByTanggalDesc(LocalDate.parse(tanggalCari), jamKerjaShiftCari, pegawaiCari, page);
                mm.addAttribute("tanggalCari", tanggalCari);
                mm.addAttribute("jamKerjaShiftCari", jamKerjaShiftCari);
                mm.addAttribute("pegawaiCari", pegawaiCari);
            }
        }

        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));
        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("jadwalKerjaShift", new JadwalKerjaShift());
        mm.addAttribute("data", result);
        return "pusat/jadwal-pegawai-shift/list";
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JadwalKerjaShift o,
                             BindingResult errors,
                             RedirectAttributes ra) {
        //log.info("JadwalKerjaShift: {}", o);

        if (o.getAdminUnit() == null) {
            ra.addFlashAttribute("message", "Admin unit tidak boleh kosong.");
            return "redirect:/pusat/jadwal-pegawai-shift/list";
        }


        if (o.getTanggal() == null) {
            ra.addFlashAttribute("message", "Tanggal tidak boleh kosong.");
            return "redirect:/pusat/jadwal-pegawai-shift/list";
        }

        if (o.getJamKerjaShift() == null) {
            ra.addFlashAttribute("message", "Jam kerja shift tidak boleh kosong.");
            return "redirect:/pusat/jadwal-pegawai-shift/list";
        }

        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/pusat/jadwal-pegawai-shift/list";
        }

        Optional<JadwalKerjaShift> jadwalKerjaShiftCek = jadwalKerjaShiftDao.findByAdminUnitAndPegawaiAndTanggal(o.getAdminUnit(), o.getPegawai(), o.getTanggal());
        if (jadwalKerjaShiftCek.isPresent()) {
            if (o.getId().equals("")) {
                ra.addFlashAttribute("message", "Pegawai sudah dipetakan untuk tanggal ini.");
                return "redirect:/pusat/jadwal-pegawai-shift/list";
            } else
                o.setId(jadwalKerjaShiftCek.get().getId());
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/pusat/jadwal-pegawai-shift/list";
        }

        jadwalKerjaShiftDao.save(o);
        return "redirect:/pusat/jadwal-pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           @PathVariable(value = "id", required = false) String id) {
        log.info("jadwal-pegawai-shift: {}", id);
        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftDao.findById(id);
        mm.addAttribute("eData", oJadwalKerjaShift.get());
        mm.addAttribute("jamKerjaShiftList", jamKerjaShiftDao.findAll(Sort.by("nama")));
        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));

        return "pusat/jadwal-pegawai-shift/list :: modalEdit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             ModelMap mm,
                             RedirectAttributes ra) {
        Optional<JadwalKerjaShift> oJadwalKerjaShift = jadwalKerjaShiftDao.findById(id);
        if (oJadwalKerjaShift.isPresent()) {
            jadwalKerjaShiftDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/jadwal-pegawai-shift/list";
    }
}
