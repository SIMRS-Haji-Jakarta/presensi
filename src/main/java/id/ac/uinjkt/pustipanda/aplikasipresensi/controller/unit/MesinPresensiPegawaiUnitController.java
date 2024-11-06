package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/admin-unit/mesin-presensi-pegawai")
public class MesinPresensiPegawaiUnitController {

    @Autowired
    private MesinPresensiPegawaiService mesinPresensiPegawaiService;

    @Autowired
    private MesinPresensiUnitService mesinPresensiUnitService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping(value = {"/", "/list"})
    public String showList(@RequestParam(value = "nama", required = false) String nama,
                           @RequestParam(value = "mesinpresensi", required = false) MesinPresensi mesinPresensi,
                           @PageableDefault(size = 10) Pageable page, ModelMap mm, Authentication auth) {
        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        List<MesinPresensiUnit> mesinPresensiUnitList = mesinPresensiUnitService.getListByAdminUnit(adminUnit);
        if (mesinPresensiUnitList.size() < 1) {
            mm.addAttribute("message", "Admin Unit ini belum dipetakan dengan Mesin Presensi");

            return "unit/mesin-presensi-pegawai/list";
        }

        Page<MesinPresensiPegawai> result = mesinPresensiPegawaiService.getFilterByNamaOrMesinPresensiByListMesinPresensiUnitAndPage(nama, mesinPresensi, mesinPresensiUnitList, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("mesinpresensi", mesinPresensi);
        mm.addAttribute("data", result);
        mm.addAttribute("listMesinPresensi", mesinPresensiService.getAllByListMesinPresensiUnit(mesinPresensiUnitList));

        return "unit/mesin-presensi-pegawai/list";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm, RedirectAttributes ra, Authentication auth) {
        AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
        if (adminUnit == null) {
            return "redirect:/";
        }

        List<MesinPresensiUnit> mesinPresensiUnitList = mesinPresensiUnitService.getListByAdminUnit(adminUnit);
        if (mesinPresensiUnitList.size() < 1) {
            ra.addFlashAttribute("message", "Admin Unit ini belum dipetakan dengan Mesin Presensi");

            return "redirect:/admin-unit/mesin-presensi-pegawai/list";
        }

        MesinPresensiPegawai mesinPresensiPegawai = new MesinPresensiPegawai();
        if (id != null && !id.isEmpty()) {
            Optional<MesinPresensiPegawai> o = mesinPresensiPegawaiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                mesinPresensiPegawai = o.get();
            }
        }

        mm.addAttribute("listMesinPresensi", mesinPresensiService.getAllByListMesinPresensiUnit(mesinPresensiUnitList));
        mm.addAttribute("mesinPresensiPegawai", mesinPresensiPegawai);
        mm.addAttribute("idAdminUnit", adminUnit.getId());

        return "unit/mesin-presensi-pegawai/list :: modalForm";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid MesinPresensiPegawaiDto o, BindingResult errors, RedirectAttributes ra,
                             ModelMap mm, Authentication auth) {
        if (errors.hasErrors()) {
            log.info("error : {}", errors);
            mm.addAttribute("mesinPresensiUnit", o);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute("message", err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return "redirect:/admin-unit/mesin-presensi-pegawai/list";
        } else {
            AdminUnit adminUnit = currentService.getCurrentAdminUnit(auth);
            if (adminUnit == null) {
                return "redirect:/";
            }

            List<MesinPresensiUnit> mesinPresensiUnitList = mesinPresensiUnitService.getListByAdminUnit(adminUnit);
            if (mesinPresensiUnitList.size() < 1) {
                ra.addFlashAttribute("message", "Admin Unit ini belum dipetakan dengan Mesin Presensi");

                return "redirect:/admin-unit/mesin-presensi-pegawai/list";
            }

            List<String> listErrNoAbsen = mesinPresensiPegawaiService.getListErrorNoAbsen(o.getPegawai());
            if (listErrNoAbsen.size() > 0) {
                ra.addFlashAttribute("message", listErrNoAbsen.stream().map(String::toString)
                        .collect(Collectors.joining("<br />")));

                return "redirect:/admin-unit/mesin-presensi-pegawai/list";
            }

            Optional<MesinPresensiPegawai> oMesinPresensiPegawai = mesinPresensiPegawaiService.findById(o.getId() == null ? -1L : o.getId());
            if (oMesinPresensiPegawai.isPresent()) {
                BeanUtils.copyProperties(o, oMesinPresensiPegawai.get());
                if (mesinPresensiPegawaiService.isDataAlreadyExist(oMesinPresensiPegawai.get())) {
                    ra.addFlashAttribute("message", "Data sudah ada");
                } else
                    mesinPresensiPegawaiService.save(oMesinPresensiPegawai.get());
            } else {
                MesinPresensiPegawai mesinPresensiPegawai = new MesinPresensiPegawai();

                mesinPresensiPegawai.setMesinPresensi(o.getMesinPresensi());
                mesinPresensiPegawai.setPegawai(o.getPegawai());

                if (mesinPresensiPegawaiService.isDataAlreadyExist(mesinPresensiPegawai)) {
                    ra.addFlashAttribute("message", "Data sudah ada");
                } else
                    mesinPresensiPegawaiService.save(mesinPresensiPegawai);
            }

            return "redirect:/admin-unit/mesin-presensi-pegawai/list";
        }
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id") String id, RedirectAttributes ra) {
        Optional<MesinPresensiPegawai> oMesinPresensiPegawai = mesinPresensiPegawaiService.findById(Long.valueOf(id));
        if (oMesinPresensiPegawai.isPresent()) {
            try {
                mesinPresensiPegawaiService.delete(oMesinPresensiPegawai.get());
            } catch (Exception e) {
                ra.addFlashAttribute("message", "Kesalahan saat menghapus data");
                log.error(e.getMessage());
            }
        }
        return "redirect:/admin-unit/mesin-presensi-pegawai/list";
    }
}