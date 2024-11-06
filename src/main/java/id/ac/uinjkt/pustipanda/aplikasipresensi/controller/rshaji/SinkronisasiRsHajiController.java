package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.rshaji;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiPegawaiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.SinkronisasiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/rshaji/sinkronisasi")
public class SinkronisasiRsHajiController {

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @Autowired
    private MesinPresensiPegawaiService mesinPresensiPegawaiService;

    @Autowired
    private SinkronisasiService sinkronisasiService;

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm) {

        mm.addAttribute("listMesinPresensiRsHaji", mesinPresensiService.findListAllMesinPresensiRsHaji());

        return "rshaji/sinkronisasi/list";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping(value = {"/proses-pegawai-mesin"})
    public String processPegawaiToMesin(@RequestParam(value = "mesinPresensi") MesinPresensi mesinPresensi,
                                        RedirectAttributes ra) {

        List<MesinPresensiPegawai> mesinPresensiPegawaiList = mesinPresensiPegawaiService.findAllByMesinPresensi(mesinPresensi);

        log.info("==== Proses pegawai ke mesin START ===");
        sinkronisasiService.prosesSinkronisasiPegawaiToMesin(mesinPresensiPegawaiList);

        ra.addFlashAttribute("messageOk", "Proses Pegawai ke Mesin Selesai");
        log.info("==== Proses Proses pegawai ke mesin END ===");

        return "redirect:/rshaji/sinkronisasi/list";
    }
}
