package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.JamKerjaShiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/jam-kerja-shift")
public class JamKerjaShiftController {
    @Autowired
    private JamKerjaShiftService jamKerjaShiftService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','rshaji')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "key", required = false) String key, @PageableDefault(size = 10) Pageable page) {
        Page<JamKerjaShift> result = jamKerjaShiftService.getFilterBySearchParamAndPage(key, page);

        mm.addAttribute("jamKerjaShift", new JamKerjaShift());
        mm.addAttribute("data", result);

        return "pusat/jam-kerja-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','rshaji')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JamKerjaShift o,
                             BindingResult errors,
                             ModelMap mm,
                             RedirectAttributes ra) {
        if (errors.hasErrors()) {
            mm.addAttribute("jamKerja", o);
            ra.addFlashAttribute("message", "Error. Terjadi kesalahan");

            return "redirect:/pusat/jam-kerja-shift/list";
        }

        if (jamKerjaShiftService.isDataAlreadyExist(o)) {
            mm.addAttribute("jamKerjaShift", o);
            ra.addFlashAttribute("message", "Error. Kode sudah digunakan");

            return "redirect:/pusat/jam-kerja-shift/list";
        }

        jamKerjaShiftService.save(o);

        return "redirect:/pusat/jam-kerja-shift/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','rshaji')")
    @GetMapping("/edit/{id}")
    public String editForm(ModelMap mm,
                           @PathVariable(value = "id", required = false) String id) {
        JamKerjaShift jamKerjaShift = new JamKerjaShift();
        if (StringUtils.hasText(id)) {
            Optional<JamKerjaShift> o = jamKerjaShiftService.findById(id);
            if (o.isPresent()) {
                jamKerjaShift = o.get();
            }
        }
        mm.addAttribute("eData", jamKerjaShift);

        return "pusat/jam-kerja-shift/list :: modalEdit";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','rshaji')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id,
                             RedirectAttributes ra) {
        Optional<JamKerjaShift> jamKerjaShift = jamKerjaShiftService.findById(id);
        if (jamKerjaShift.isPresent()) {
            try {
                jamKerjaShiftService.delete(jamKerjaShift.get());
            } catch (Exception e) {
                log.error("Error saat menghapus: " + e.getMessage());
                ra.addFlashAttribute("message", "Terjadi Kesalahan! Data tidak dapat dihapus");
            }
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/jam-kerja-shift/list";
    }

}
