package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.UnitDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.RoleService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/pusat/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ValidationService validationService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "q", required = false) String search,
                           @PageableDefault(size = 10) Pageable page) {
        Page<Unit> result = unitService.getAllDataBySearchParam(search, page);
        log.info("search: {}", search);

        mm.addAttribute("q", search);
        mm.addAttribute("data", result);

        return AppConstant.TEMPLATE_UNIT;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        Unit unit = new Unit();
        if (id != null && !id.isEmpty()) {
            Optional<Unit> o = unitService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                unit = o.get();
            }
        }

        mm.addAttribute("data", unit.getId() == null ? new UnitDto() : unit);

        return AppConstant.TEMPLATE_UNIT + " :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid UnitDto o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {
        log.info("error : {}", errors);

        if (errors.hasErrors()) {
            log.info("errors {}", errors);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute(AppConstant.MESSAGE, err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return AppConstant.LINK_UNIT;
        }

        if (o.getId() == null)
            o.setId(0L);

        Optional<Unit> oUnit = unitService.findById(o.getId());
        if (oUnit.isPresent()) {
            Optional<Unit> checkUnitIsExist = unitService.findByUnitNamaAndIdNot(o.getNama().toLowerCase(), o.getId());
            if (checkUnitIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_UNIT;
            }

            BeanUtils.copyProperties(o, oUnit.get(), "id");

            unitService.save(oUnit.get());
        } else {
            Optional<Unit> checkUnitIsExist = unitService.findByUnitNama(o.getNama().toLowerCase());
            if (checkUnitIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_UNIT;
            }

            Unit unit = new Unit();
            BeanUtils.copyProperties(o, unit);
            unitService.save(unit);
        }

        return AppConstant.LINK_UNIT;
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) Long id, RedirectAttributes ra) {
        Optional<Unit> unit = unitService.findById(id);
        if (unit.isPresent()) {
            try {
                unitService.delete(unit.get());
            } catch (Exception e) {
                log.error("error delete unit: {}", e.getMessage());
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak dapat dihapus");
            }

        } else {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak ditemukan");
        }

        return AppConstant.LINK_UNIT;
    }
}
