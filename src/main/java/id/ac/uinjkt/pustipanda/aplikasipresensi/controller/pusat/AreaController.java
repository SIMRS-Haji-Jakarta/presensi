package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AreaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Konfigurasi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Area;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.KonfigurasiService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/area")
public class AreaController {
    @Autowired
    private AreaDao areaDao;

    @Autowired
    private KonfigurasiService konfigurasiService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "key", required = false) String key, @PageableDefault(size = 10) Pageable page) {
        Page<Area> result;
        Konfigurasi konfigurasiAbsenByArea = konfigurasiService.getKonfigurasi(AppConstant.ABSEN_BY_AREA);

        if (key != null) {
            result = areaDao.findByNamaContainingIgnoreCaseOrderByIdAsc(key, page);
            mm.addAttribute("key", key);
        } else {
            result = areaDao.findAllByOrderByIdAsc(page);
        }

        mm.addAttribute("statusAbsenByArea", konfigurasiAbsenByArea != null
                && konfigurasiAbsenByArea.getNilai().equals(AppConstant.AKTIF));
        mm.addAttribute("data", result);

        return "pusat/area/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        Area area = new Area();
        if (id != null) {
            Optional<Area> o = areaDao.findById(id);
            if (o.isPresent()) {
                area = o.get();
            }
        }
        mm.addAttribute("area", area);

        return "pusat/area/form";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid Area o, BindingResult errors,
                             ModelMap mm) {
        if (errors.hasErrors()) {
            log.info("error : {}", errors);
            mm.addAttribute("area", o);

            return "pusat/area/form";
        } else {
            String cleanKoordinat = o.getKoordinat().replaceAll("\\s+", ";"); //remove whitespace change to ;
            o.setKoordinat(cleanKoordinat);
            areaDao.save(o);

            return "redirect:/pusat/area/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/modal/{id}")
    public String showModal(@PathVariable String id, ModelMap mm) {
        Optional<Area> oArea = areaDao.findById(id);
        if (oArea.isPresent()) {
            mm.addAttribute("area", oArea.get());

            return "pusat/area/list::areaModal";
        }
        return "redirect:/pusat/area/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/update-status")
    @ResponseBody
    public Map<String, Object> updateStatus() {
        Konfigurasi konfigurasiStatusByArea = konfigurasiService.getKonfigurasi(AppConstant.ABSEN_BY_AREA);
        Map<String, Object> hasil = new HashMap<>();
        hasil.put("status", false);
        if (konfigurasiStatusByArea != null) {
            if (konfigurasiStatusByArea.getNilai().equals(AppConstant.AKTIF)) {
                konfigurasiStatusByArea.setNilai(AppConstant.TIDAK_AKTIF);
            } else {
                konfigurasiStatusByArea.setNilai(AppConstant.AKTIF);
            }
            konfigurasiService.save(konfigurasiStatusByArea);
            hasil.put("status", true);
        }
        return hasil;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id") String id, RedirectAttributes ra) {
        Optional<Area> oArea = areaDao.findById(id);
        if (oArea.isPresent()) {
            try {
                areaDao.delete(oArea.get());
            } catch (Exception e) {
                ra.addFlashAttribute("message", "Kesalahan saat menghapus data");
                log.error(e.getMessage());
            }
        }
        return "redirect:/pusat/area/list";
    }

}
