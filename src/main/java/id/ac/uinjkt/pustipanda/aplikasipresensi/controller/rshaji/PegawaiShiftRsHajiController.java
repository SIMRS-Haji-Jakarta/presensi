package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.rshaji;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PegawaiShiftService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitKerjaPresensiService;
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
@RequestMapping("/rshaji/pegawai-shift")
public class PegawaiShiftRsHajiController {
    @Autowired
    private PegawaiShiftService pegawaiShiftService;

    @Autowired
    private UnitKerjaPresensiService unitKerjaPresensiService;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "nama", required = false) String nama,
                           @PageableDefault(size = 10) Pageable page) {

        Optional<UnitKerjaPresensi> unitRsHaji = unitKerjaPresensiService.getById(UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI); // Rumah Sakit Haji
        Page<PegawaiShift> result = pegawaiShiftService.getFilterBySearchParamAndPage(nama, unitRsHaji.get(), page);

        mm.addAttribute("adminUnitList", adminUnitDao.findAll(Sort.by("nama")));
        mm.addAttribute("pegawaiShift", new PegawaiShift());
        mm.addAttribute("data", result);
        mm.addAttribute("idUnitKerja", UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI);

        return "rshaji/pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid PegawaiShift o,
                             BindingResult errors,
                             RedirectAttributes ra) {
        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/rshaji/pegawai-shift/list";
        }

//        if (o.getAdminUnit() == null) {
//            ra.addFlashAttribute("message", "Admin Unit tidak boleh kosong.");
//            return "redirect:/rshaji/pegawai-shift/list";
//        }

        if (pegawaiUtils.isExistPegawaiShiftAdminUnit(o.getAdminUnit(), o.getPegawai())) {
            ra.addFlashAttribute("message", "Pegawai untuk Admin Unit ini sudah ada.");
            return "redirect:/rshaji/pegawai-shift/list";
        }

        if (errors.hasErrors()) {
            log.info("error : {}", errors.toString());
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/rshaji/pegawai-shift/list";
        }

        pegawaiShiftService.save(o);
        return "redirect:/rshaji/pegawai-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             ModelMap mm,
                             RedirectAttributes ra) {
        Optional<PegawaiShift> pegawaiShift = pegawaiShiftService.findById(id);
        if (pegawaiShift.isPresent()) {
            try {
                pegawaiShiftService.delete(pegawaiShift.get());
            } catch (Exception e) {
                log.error("Error : " + e.getMessage());
                ra.addFlashAttribute("message", "Terjadi Kesalahan! Data tidak dapat dihapus");
            }
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/rshaji/pegawai-shift/list";
    }
}
