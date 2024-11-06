package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiPegawaiService;
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
@RequestMapping("/pusat/mesin-presensi-pegawai")
public class MesinPresensiPegawaiController {

    @Autowired
    private MesinPresensiPegawaiService mesinPresensiPegawaiService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(@RequestParam(value = "nama", required = false) String nama,
                           @RequestParam(value = "namamesin", required = false) String namaMesin,
                           @PageableDefault(size = 10) Pageable page, ModelMap mm) {

        Page<MesinPresensiPegawai> result = mesinPresensiPegawaiService.getFilterBySearchParamAndPage(nama, namaMesin, null, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("namamesin", namaMesin);
        mm.addAttribute("data", result);

        return "pusat/mesin-presensi-pegawai/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        MesinPresensiPegawai mesinPresensiPegawai = new MesinPresensiPegawai();
        if (id != null && !id.isEmpty()) {
            Optional<MesinPresensiPegawai> o = mesinPresensiPegawaiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                mesinPresensiPegawai = o.get();
            }
        }

        //mm.addAttribute("listAdminUnit", adminUnitService.findListAllAdminUnit());
        mm.addAttribute("listMesinPresensi", mesinPresensiService.findListAllMesinPresensi());
        mm.addAttribute("mesinPresensiPegawai", mesinPresensiPegawai);

        return "pusat/mesin-presensi-pegawai/list :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid MesinPresensiPegawaiDto o, BindingResult errors, RedirectAttributes ra,
                             ModelMap mm) {
        if (errors.hasErrors()) {
            log.info("error : {}", errors);
            mm.addAttribute("mesinPresensiUnit", o);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute("message", err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return "redirect:/pusat/mesin-presensi-pegawai/list";
        } else {
            List<String> listErrNoAbsen = mesinPresensiPegawaiService.getListErrorNoAbsen(o.getPegawai());
            if (listErrNoAbsen.size() > 0) {
                ra.addFlashAttribute("message", listErrNoAbsen.stream().map(String::toString)
                        .collect(Collectors.joining("<br />")));

                return "redirect:/pusat/mesin-presensi-pegawai/list";
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

            return "redirect:/pusat/mesin-presensi-pegawai/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
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
        return "redirect:/pusat/mesin-presensi-pegawai/list";
    }
}
