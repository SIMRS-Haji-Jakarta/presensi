package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerja;
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
@RequestMapping("/pusat/jam-kerja")
public class JamKerjaController {
    @Autowired
    private JamKerjaDao jamKerjaDao;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "key", required = false) String key, @PageableDefault(size = 10) Pageable page) {
        Page<JamKerja> result;

        if (key != null) {
            result = jamKerjaDao.findByNamaContainingIgnoreCase(key, page);
            mm.addAttribute("key", key);
        } else {
            result = jamKerjaDao.findAll(page);
        }
        mm.addAttribute("data", result);

        return "pusat/jam-kerja/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        JamKerja jamKerja = new JamKerja();
        if (StringUtils.hasText(id)) {
            Optional<JamKerja> o = jamKerjaDao.findById(id);
            if (o.isPresent()) {
                jamKerja = o.get();
            }
        }
        mm.addAttribute("jamKerja", jamKerja);

        return "pusat/jam-kerja/form";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JamKerja o, BindingResult errors, ModelMap mm) {
        log.info("error : {}", errors);
        if (errors.hasErrors()) {
            mm.addAttribute("jamKerja", o);

            return "pusat/jam-kerja/form";
        } else {
            jamKerjaDao.save(o);

            return "redirect:/pusat/jam-kerja/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<JamKerja> jamKerja = jamKerjaDao.findById(id);
        if (jamKerja.isPresent()) {
            jamKerjaDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/jam-kerja/list";
    }

}
