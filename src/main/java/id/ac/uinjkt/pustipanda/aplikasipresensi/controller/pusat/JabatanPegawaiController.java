package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.JabatanPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JabatanPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.JabatanPegawaiService;
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
@RequestMapping("/pusat/jabatan")
public class JabatanPegawaiController {

    @Autowired
    private JabatanPegawaiService jabatanPegawaiService;

    @Autowired
    private ValidationService validationService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "q", required = false) String search,
                           @PageableDefault(size = 10) Pageable page) {
        Page<JabatanPegawai> result = jabatanPegawaiService.getAllDataBySearchParam(search, page);
        log.info("search: {}", search);

        mm.addAttribute("q", search);
        mm.addAttribute("data", result);

        return AppConstant.TEMPLATE_JABATAN;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        JabatanPegawai jabatanPegawai = new JabatanPegawai();
        if (id != null && !id.isEmpty()) {
            Optional<JabatanPegawai> o = jabatanPegawaiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                jabatanPegawai = o.get();
            }
        }

        mm.addAttribute("data", jabatanPegawai.getId() == null ? new JabatanPegawaiDto() : jabatanPegawai);

        return AppConstant.TEMPLATE_JABATAN + " :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid JabatanPegawaiDto o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {
        log.info("error : {}", errors);

        if (errors.hasErrors()) {
            log.info("errors {}", errors);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute(AppConstant.MESSAGE, err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return AppConstant.LINK_JABATAN;
        }

        if (o.getId() == null)
            o.setId(0L);

        Optional<JabatanPegawai> oJabatanPegawai = jabatanPegawaiService.findById(o.getId());
        if (oJabatanPegawai.isPresent()) {
            Optional<JabatanPegawai> checkJabatanPegawaiIsExist = jabatanPegawaiService.findByJabatanPegawaiNamaAndIdNot(o.getNama().toLowerCase(), o.getId());
            if (checkJabatanPegawaiIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_JABATAN;
            }

            BeanUtils.copyProperties(o, oJabatanPegawai.get(), "id");

            jabatanPegawaiService.save(oJabatanPegawai.get());
        } else {
            Optional<JabatanPegawai> checkJabatanPegawaiIsExist = jabatanPegawaiService.findByJabatanPegawaiNama(o.getNama().toLowerCase());
            if (checkJabatanPegawaiIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_JABATAN;
            }

            JabatanPegawai jabatanPegawai = new JabatanPegawai();
            BeanUtils.copyProperties(o, jabatanPegawai);
            jabatanPegawaiService.save(jabatanPegawai);
        }

        return AppConstant.LINK_JABATAN;
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) Long id, RedirectAttributes ra) {
        Optional<JabatanPegawai> jabatanPegawai = jabatanPegawaiService.findById(id);
        if (jabatanPegawai.isPresent()) {
            try {
                jabatanPegawaiService.delete(jabatanPegawai.get());
            } catch (Exception e) {
                log.error("error delete jabatan: {}", e.getMessage());
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak dapat dihapus");
            }

        } else {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak ditemukan");
        }

        return AppConstant.LINK_JABATAN;
    }
}
