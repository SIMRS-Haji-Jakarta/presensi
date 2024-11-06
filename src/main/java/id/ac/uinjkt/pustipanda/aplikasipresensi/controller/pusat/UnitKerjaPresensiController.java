package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.UnitKerjaPresensiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitKerjaPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitService;
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
@RequestMapping("/pusat/unit-kerja")
public class UnitKerjaPresensiController {

    @Autowired
    private UnitKerjaPresensiService unitKerjaPresensiService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UnitService unitService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "q", required = false) String search,
                           @RequestParam(value = "parent", required = false) UnitKerjaPresensi parent,
                           @RequestParam(value = "unit", required = false) Unit unit,
                           @PageableDefault(size = 10) Pageable page) {
        Page<UnitKerjaPresensi> result = unitKerjaPresensiService.getAllDataBySearchParam(search, unit, parent, page);
        log.info("search: {}", search);

        mm.addAttribute("q", search);
        mm.addAttribute("parent", parent);
        mm.addAttribute("unit", unit);
        mm.addAttribute("listUnitKerja", unitKerjaPresensiService.findAll());
        mm.addAttribute("listUnit", unitService.findAll());
        mm.addAttribute("data", result);

        return AppConstant.TEMPLATE_UNIT_KERJA;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        UnitKerjaPresensi unitKerjaPresensi = new UnitKerjaPresensi();
        if (id != null && !id.isEmpty()) {
            Optional<UnitKerjaPresensi> o = unitKerjaPresensiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                unitKerjaPresensi = o.get();
            }
        }

        mm.addAttribute("data", unitKerjaPresensi.getId() == null ? new UnitKerjaPresensiDto() : unitKerjaPresensi);
        mm.addAttribute("listUnitKerja", unitKerjaPresensiService.findAll());
        mm.addAttribute("listUnit", unitService.findAll());

        return AppConstant.TEMPLATE_UNIT_KERJA + " :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid UnitKerjaPresensiDto o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {
        log.info("error : {}", errors);

        if (errors.hasErrors()) {
            log.info("errors {}", errors);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute(AppConstant.MESSAGE, err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return AppConstant.LINK_UNIT_KERJA;
        }

        if (o.getId() == null)
            o.setId(0L);

        Optional<UnitKerjaPresensi> oUnitKerjaPresensi = unitKerjaPresensiService.findById(o.getId());
        if (oUnitKerjaPresensi.isPresent()) {
            Optional<UnitKerjaPresensi> checkUnitKerjaPresensiIsExist = unitKerjaPresensiService.findByNamaAndUnitAndParentAndIdNot(o.getNama().toLowerCase(), o.getUnit(), o.getParent(), o.getId());
            if (checkUnitKerjaPresensiIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_UNIT_KERJA;
            }

            BeanUtils.copyProperties(o, oUnitKerjaPresensi.get(), "id");

            unitKerjaPresensiService.save(oUnitKerjaPresensi.get());
        } else {
            Optional<UnitKerjaPresensi> checkUnitKerjaPresensiIsExist = unitKerjaPresensiService.findByNamaAndUnitAndParent(o.getNama().toLowerCase(), o.getUnit(), o.getParent());
            if (checkUnitKerjaPresensiIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_UNIT_KERJA;
            }

            UnitKerjaPresensi unitKerjaPresensi = new UnitKerjaPresensi();
            BeanUtils.copyProperties(o, unitKerjaPresensi);
            unitKerjaPresensiService.save(unitKerjaPresensi);
        }

        return AppConstant.LINK_UNIT_KERJA;
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) Long id, RedirectAttributes ra) {
        Optional<UnitKerjaPresensi> unitKerjaPresensi = unitKerjaPresensiService.findById(id);
        if (unitKerjaPresensi.isPresent()) {
            try {
                unitKerjaPresensiService.delete(unitKerjaPresensi.get());
            } catch (Exception e) {
                log.error("error delete unitKerjaPresensi: {}", e.getMessage());
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak dapat dihapus");
            }

        } else {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak ditemukan");
        }

        return AppConstant.LINK_UNIT_KERJA;
    }
}
