package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/admin-unit/pegawai-shift-unit")
public class PegawaiShiftUnitController {

    @Autowired
    private PegawaiShiftDao pegawaiShiftDao;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           Authentication auth,
                           @RequestParam(value = "noAbsen", required = false, defaultValue = "") String noAbsen,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @PageableDefault(size = 10) Pageable page) {

        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        Page<PegawaiShift> result;

        log.info("noAbsen : {}, nama pegawai {}", noAbsen, pegawaiCari);

        if (!noAbsen.equals("")) {
            result = pegawaiShiftDao.findAllByAdminUnitAndPegawai_NomorAbsenContainingIgnoreCase(adminUnit, noAbsen, page);
            mm.addAttribute("noAbsen", noAbsen);
        } else if (pegawaiCari != null) {
            result = pegawaiShiftDao.findAllByAdminUnitAndPegawai(adminUnit, pegawaiCari, page);
            mm.addAttribute("pegawaiCari", pegawaiCari);
        } else {
            result = pegawaiShiftDao.findAllByAdminUnit(adminUnit, page);
        }

        mm.addAttribute("pegawaiShift", new PegawaiShift());
        mm.addAttribute("data", result);
        mm.addAttribute("idAdminUnit", adminUnit.getId());
        return "unit/pegawai-shift-unit/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid PegawaiShift o,
                             BindingResult errors,
                             Authentication auth,
                             RedirectAttributes ra) {
        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/admin-unit/pegawai-shift-unit/list";
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/admin-unit/pegawai-shift-unit/list";
        }

        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit != null) {
            if (pegawaiUtils.isExistPegawaiShiftAdminUnit(adminUnit, o.getPegawai())) {
                ra.addFlashAttribute("message", "Data sudah ada");
                return "redirect:/admin-unit/pegawai-shift-unit/list";
            }
            o.setAdminUnit(adminUnit);
            pegawaiShiftDao.save(o);
        }

        return "redirect:/admin-unit/pegawai-shift-unit/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           Authentication auth,
                           @PathVariable(value = "id", required = false) String id) {
        log.info("admin unit: {}", id);

        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findById(id);
        mm.addAttribute("eData", pegawaiShift.get());

        return "unit/pegawai-shift-unit/list :: modalEdit";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             ModelMap mm,
                             Authentication auth) {
        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findByAdminUnitAndId(adminUnit, id);
        if (pegawaiShift.isPresent()) {
            pegawaiShiftDao.deleteById(id);
        } else {
            mm.addAttribute("message", "Data bukan milik Admin Unit " + adminUnit.getNama());
        }

        return "redirect:/admin-unit/pegawai-shift-unit/list";
    }
}
