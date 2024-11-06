package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/pusat/mesin-presensi")
public class MesinPresensiController {
    @Autowired
    private MesinPresensiService mesinPresensiService;

    @Autowired
    private ValidationService validationService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(@RequestParam(value = "nama", required = false) String nama,
                           @RequestParam(value = "kodearea", required = false) String kodeArea,
                           @PageableDefault(size = 10) Pageable page, ModelMap mm) {

        Page<MesinPresensi> result = mesinPresensiService.getFilterByNamaOrKodeAreaAndPage(nama, kodeArea, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("kodearea", kodeArea);
        mm.addAttribute("data", result);

        return "pusat/mesin-presensi/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        MesinPresensi mesinPresensi = new MesinPresensi();
        if (id != null && !id.isEmpty()) {
            Optional<MesinPresensi> o = mesinPresensiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                mesinPresensi = o.get();
            }
        }
        mm.addAttribute("mesinPresensi", mesinPresensi);

        return "pusat/mesin-presensi/list :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid MesinPresensiDto o, BindingResult errors, RedirectAttributes ra,
                             ModelMap mm) {
        if (errors.hasErrors()) {
            log.info("error : {}", errors);
            mm.addAttribute("mesinPresensi", o);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute("message", err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return "redirect:/pusat/mesin-presensi/list";
        } else {
            if (mesinPresensiService.isDataAlreadyExist(o)) {
                ra.addFlashAttribute("message", "Kode area sudah ada");
                return "redirect:/pusat/mesin-presensi/list";
            }

            Optional<MesinPresensi> oMesinPresensi = mesinPresensiService.findById(o.getId() == null ? -1L : o.getId());
            if (oMesinPresensi.isPresent()) {
                BeanUtils.copyProperties(o, oMesinPresensi.get());
                mesinPresensiService.save(oMesinPresensi.get());
            } else {
                MesinPresensi mesinPresensi = new MesinPresensi();

                mesinPresensi.setLokasi(o.getLokasi());
                mesinPresensi.setIpAddress(o.getIpAddress());
                mesinPresensi.setNama(o.getNama());
                mesinPresensi.setKodeArea(o.getKodeArea());
                mesinPresensi.setSn(o.getSn());
                mesinPresensiService.save(mesinPresensi);
            }

            return "redirect:/pusat/mesin-presensi/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id") String id, RedirectAttributes ra) {
        Optional<MesinPresensi> oMesinPresensi = mesinPresensiService.findById(Long.valueOf(id));
        if (oMesinPresensi.isPresent()) {
            try {
                mesinPresensiService.delete(oMesinPresensi.get());
            } catch (Exception e) {
                ra.addFlashAttribute("message", "Kesalahan saat menghapus data");
                log.error(e.getMessage());
            }
        }
        return "redirect:/pusat/mesin-presensi/list";
    }
}
