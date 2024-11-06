package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FacePegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FacePegawaiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/face-pegawai")
public class FacePegawaiController {
    @Autowired
    private FacePegawaiService facePegawaiService;

    @Autowired
    private UnitService unitService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(@RequestParam(value = "nama", required = false) String nama,
                           @RequestParam(value = "unit", required = false) Unit unit,
                           @PageableDefault(size = 10) Pageable page, ModelMap mm) {

        Page<FacePegawai> result = facePegawaiService.getFilterByNamaOrUnitAndPage(nama, unit, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("unit", unit);
        mm.addAttribute("listUnit", unitService.findAll());
        mm.addAttribute("data", result);

        return "pusat/face-pegawai/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id") String id, RedirectAttributes ra) {
        Optional<FacePegawai> oFacePegawai = facePegawaiService.findById(id);
        if (oFacePegawai.isPresent()) {
            try {
                facePegawaiService.delete(oFacePegawai.get());
            } catch (Exception e) {
                ra.addFlashAttribute("message", "Kesalahan saat menghapus data");
                log.error(e.getMessage());
            }
        }
        return "redirect:/pusat/face-pegawai/list";
    }
}
