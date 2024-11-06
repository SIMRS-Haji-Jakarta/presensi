package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
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
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/pegawai-shift")
public class PegawaiShiftController {
    @Autowired
    private PegawaiShiftDao pegawaiShiftDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "adminUnitCari", required = false) AdminUnit adminUnitCari,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @PageableDefault(size = 10) Pageable page) {

        Page<PegawaiShift> result;

        // log.info("admin unit : {}, nama pegawai {}", adminUnitCari, pegawaiCari);

        if (adminUnitCari != null) {
            result = pegawaiShiftDao.findAllByAdminUnit(adminUnitCari, page);
            mm.addAttribute("adminUnitCari", adminUnitCari);
        } else if (pegawaiCari != null) {
            result = pegawaiShiftDao.findAllByPegawai(pegawaiCari, page);
            mm.addAttribute("pegawaiCari", pegawaiCari);
        } else {
            result = pegawaiShiftDao.findAll(page);
        }

        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));
        mm.addAttribute("pegawaiShift", new PegawaiShift());
        mm.addAttribute("data", result);
        return "pusat/pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid PegawaiShift o,
                             BindingResult errors,
                             RedirectAttributes ra) {
        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/pusat/pegawai-shift/list";
        }

        if (o.getAdminUnit() == null) {
            ra.addFlashAttribute("message", "Admin Unit tidak boleh kosong.");
            return "redirect:/pusat/pegawai-shift/list";
        }

        if (pegawaiUtils.isExistPegawaiShiftAdminUnit(o.getAdminUnit(), o.getPegawai())) {
            ra.addFlashAttribute("message", "Pegawai untuk Admin Unit ini sudah ada.");
            return "redirect:/pusat/pegawai-shift/list";
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/pusat/pegawai-shift/list";
        }

        pegawaiShiftDao.save(o);
        return "redirect:/pusat/pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           @PathVariable(value = "id", required = false) String id) {
        log.info("admin unit: {}", id);
        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findById(id);
        mm.addAttribute("eData", pegawaiShift.get());
        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));

        return "pusat/pegawai-shift/list :: modalEdit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             ModelMap mm,
                             RedirectAttributes ra) {
        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findById(id);
        if (pegawaiShift.isPresent()) {
            pegawaiShiftDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/pegawai-shift/list";
    }
}
