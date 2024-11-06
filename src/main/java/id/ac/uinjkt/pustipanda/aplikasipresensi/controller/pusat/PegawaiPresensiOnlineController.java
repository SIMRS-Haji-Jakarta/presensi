package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiPresensiOnline;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PegawaiPresensiOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/pegawai-presensi-online")
public class PegawaiPresensiOnlineController {
    @Autowired
    private UnitDao unitDao;

    @Autowired
    private PegawaiPresensiOnlineService pegawaiPresensiOnlineService;

    @Autowired
    private CurrentService currentService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "nama", required = false) String nama,
                           @RequestParam(value = "unit", required = false) Unit unit,
                           @PageableDefault(size = 10) Pageable page) {

        Page<PegawaiPresensiOnline> result = pegawaiPresensiOnlineService.getFilterBySearchParamAndPage(nama, unit, page);

        mm.addAttribute("nama", nama);
        mm.addAttribute("unit", unit);
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));
        mm.addAttribute("data", result);
        mm.addAttribute("pegawaiPresensi", new PegawaiPresensiOnline());

        return "pusat/pegawai-presensi-online/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid PegawaiPresensiOnline o,
                             RedirectAttributes ra, Authentication auth) {
        if (o.getPegawai() == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/pusat/pegawai-presensi-online/list";
        }

        if (pegawaiPresensiOnlineService.isDataAlreadyExist(o)) {
            ra.addFlashAttribute("message", "Data sudah ada.");
            return "redirect:/pusat/pegawai-presensi-online/list";
        }

        o.setOleh(currentService.getCurrentUser(auth).getPegawai().getNama());
        o.setTanggal(LocalDateTime.now());

        pegawaiPresensiOnlineService.save(o);

        return "redirect:/pusat/pegawai-presensi-online/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String PegawaiPresensiOnline(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<PegawaiPresensiOnline> data = pegawaiPresensiOnlineService.findById(id);
        if (data.isPresent()) {
            pegawaiPresensiOnlineService.delete(data.get());
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/pegawai-presensi-online/list";
    }
}
