package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AbsensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Absensi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/pusat/reset")
public class ResetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetController.class);

    @Autowired
    private AbsensiDao absensiDao;


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "nama", required = false) String nama, @RequestParam(value = "tanggal", required = false) String tanggal, @PageableDefault(size = 10) Pageable page) {
        Page<Absensi> result;

        if (tanggal != null) {
            if (nama != null) {
                result = absensiDao.findAllByTanggalAndPegawaiNamaContainingIgnoreCase(LocalDate.parse(tanggal), nama, page);
                mm.addAttribute("nama", nama);
            } else {
                result = absensiDao.findAllByTanggal(LocalDate.parse(tanggal), page);
            }
            mm.addAttribute("tanggal", tanggal);
        } else {
            result = absensiDao.findAllByOrderByTanggalDesc(page);
        }
        mm.addAttribute("data", result);
        return "pusat/reset/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/datang")
    public String resetDatang(@RequestParam Absensi absensi) {
        if (absensi == null) {
            LOGGER.warn("absensi null");
            return "redirect:/pusat/reset/list";
        }

        absensi.setWaktuIn(null);
        absensiDao.save(absensi);

        return "redirect:/pusat/reset/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/pulang")
    public String resetPulang(@RequestParam Absensi absensi) {
        if (absensi == null) {
            LOGGER.warn("absensi null");
            return "redirect:/pusat/reset/list";
        }

        absensi.setWaktuOut(null);
        absensiDao.save(absensi);

        return "redirect:/pusat/reset/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<Absensi> absensi = absensiDao.findById(id);
        if (absensi.isPresent()) {
            absensiDao.deleteById(id);
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/reset/list";
    }

}
