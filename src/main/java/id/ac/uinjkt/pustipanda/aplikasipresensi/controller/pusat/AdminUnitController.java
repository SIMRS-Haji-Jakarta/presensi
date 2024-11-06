package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitKerjaPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/pusat/admin-unit")
public class AdminUnitController {
    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private UnitKerjaPresensiDao unitKerjaDao;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "nama", required = false, defaultValue = "") String nama,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @PageableDefault(size = 10) Pageable page) {
        Page<AdminUnit> result;

        log.info("nama admin unit: {}, nama pegawai {}", nama, pegawaiCari);

        if (!nama.equals("")) {
            result = adminUnitDao.findAllByNamaContainingIgnoreCase(nama, page);
            mm.addAttribute("nama", nama);
        } else if (pegawaiCari != null) {
            result = adminUnitDao.findAllByPegawaiOrderByPegawaiId(pegawaiCari, page);
            mm.addAttribute("pegawaiCari", pegawaiCari);
        } else {
            result = adminUnitDao.findAllByOrderByIdAsc(page);
        }

        mm.addAttribute("adminunit", new AdminUnit());
        mm.addAttribute("listUnitkerja", unitKerjaDao.findAll());
        mm.addAttribute("data", result);
        return "pusat/admin-unit/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid AdminUnit o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {

        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/pusat/admin-unit/list";
        }

        if (o.getPjPegawai() == null) {
            ra.addFlashAttribute("message", "Pejabat tidak boleh kosong.");
            return "redirect:/pusat/admin-unit/list";
        }

        if (o.getPjJabatan() == null) {
            ra.addFlashAttribute("message", "Jabatan tidak boleh kosong.");
            return "redirect:/pusat/admin-unit/list";
        }

        if (o.getNama() == null) {
            ra.addFlashAttribute("message", "Nama tidak boleh kosong.");
            return "redirect:/pusat/admin-unit/list";
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/pusat/admin-unit/list";
        }

        adminUnitDao.save(o);

        return "redirect:/pusat/admin-unit/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/edit/{id}")
    public String showAdminUnit(ModelMap mm, @PathVariable(value = "id", required = false) String id) {
        log.info("admin unit: {}", id);
        Optional<AdminUnit> adminUnit = adminUnitDao.findById(id);
        mm.addAttribute("a", adminUnit.get());
        mm.addAttribute("listUnitkerja", unitKerjaDao.findAll());

        return "pusat/admin-unit/list :: modalEditAdminUnit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, ModelMap mm) {
        Optional<AdminUnit> adminUnit = adminUnitDao.findById(id);
        if (adminUnit.isPresent()) {
            adminUnitDao.deleteById(id);
        } else {
            mm.addAttribute("message", "Data bukan milik Admin Unit " + adminUnit.get().getNama());
        }

        return "redirect:/pusat/admin-unit/list";
    }
}
