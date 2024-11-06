package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiUnitDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.AdminUnitService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiUnitService;
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
@RequestMapping("/pusat/mesin-presensi-unit")
public class MesinPresensiUnitController {

    @Autowired
    private MesinPresensiUnitService mesinPresensiUnitService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AdminUnitService adminUnitService;

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "nama", required = false) String nama, @PageableDefault(size = 10) Pageable page) {
        Page<MesinPresensiUnit> result = mesinPresensiUnitService.findAllPageMesinPresensiUnit(nama, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("data", result);

        return "pusat/mesin-presensi-unit/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        MesinPresensiUnit mesinPresensiUnit = new MesinPresensiUnit();
        if (id != null && !id.isEmpty()) {
            Optional<MesinPresensiUnit> o = mesinPresensiUnitService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                mesinPresensiUnit = o.get();
            }
        }

        mm.addAttribute("listAdminUnit", adminUnitService.findListAllAdminUnit());
        mm.addAttribute("listMesinPresensi", mesinPresensiService.findListAllMesinPresensi());
        mm.addAttribute("mesinPresensiUnit", mesinPresensiUnit);

        return "pusat/mesin-presensi-unit/list :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid MesinPresensiUnitDto o, BindingResult errors, RedirectAttributes ra,
                             ModelMap mm) {
        if (errors.hasErrors()) {
            log.info("error : {}", errors);
            mm.addAttribute("mesinPresensiUnit", o);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute("message", err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return "redirect:/pusat/mesin-presensi-unit/list";
        } else {
            Optional<MesinPresensiUnit> oMesinPresensiUnit = mesinPresensiUnitService.findById(o.getId() == null ? -1L : o.getId());
            if (oMesinPresensiUnit.isPresent()) {
                BeanUtils.copyProperties(o, oMesinPresensiUnit.get());
                if (mesinPresensiUnitService.isDataAlreadyExist(oMesinPresensiUnit.get())) {
                    ra.addFlashAttribute("message", "Data sudah ada");
                } else
                    mesinPresensiUnitService.save(oMesinPresensiUnit.get());
            } else {
                MesinPresensiUnit mesinPresensiUnit = new MesinPresensiUnit();

                mesinPresensiUnit.setMesinPresensi(o.getMesinPresensi());
                mesinPresensiUnit.setAdminUnit(o.getAdminUnit());

                if (mesinPresensiUnitService.isDataAlreadyExist(mesinPresensiUnit)) {
                    ra.addFlashAttribute("message", "Data sudah ada");
                } else
                    mesinPresensiUnitService.save(mesinPresensiUnit);
            }

            return "redirect:/pusat/mesin-presensi-unit/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id") String id, RedirectAttributes ra) {
        Optional<MesinPresensiUnit> oMesinPresensiUnit = mesinPresensiUnitService.findById(Long.valueOf(id));
        if (oMesinPresensiUnit.isPresent()) {
            try {
                mesinPresensiUnitService.delete(oMesinPresensiUnit.get());
            } catch (Exception e) {
                ra.addFlashAttribute("message", "Kesalahan saat menghapus data");
                log.error(e.getMessage());
            }
        }
        return "redirect:/pusat/mesin-presensi-unit/list";
    }
}
