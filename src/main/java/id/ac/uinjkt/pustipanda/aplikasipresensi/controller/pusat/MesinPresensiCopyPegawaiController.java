package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeMesinPresensiPegawaiDto;
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
@RequestMapping("/pusat/mesin-presensi-copy-pegawai")
public class MesinPresensiCopyPegawaiController {

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @Autowired
    private MesinPresensiPegawaiService mesinPresensiPegawaiService;

    @Autowired
    private SinkronisasiService sinkronisasiService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm) {

        mm.addAttribute("listMesinPresensi", mesinPresensiService.findListAllMesinPresensi());

        return "pusat/mesin-presensi-copy-pegawai/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/proses"})
    public String process(ModelMap mm, @RequestParam(value = "mesinPresensiSumber", required = false) MesinPresensi mesinPresensiSumber,
                          @RequestParam(value = "mesinPresensiTujuan", required = false) MesinPresensi mesinPresensiTujuan,
                          @RequestParam(value = "tipePegawai", required = false) Long tipePegawai, RedirectAttributes ra) {

        if (mesinPresensiSumber == null || mesinPresensiTujuan == null || tipePegawai == null) {
            ra.addFlashAttribute("messageErr", "Pastikan pilihan tidak ada yang kosong!");
            return "redirect:/pusat/mesin-presensi-copy-pegawai/list";
        }

        log.info("==== Proses copy pegawai START ===");
        List<EmployeeMesinPresensiPegawaiDto> pegawaiIdListSumber = mesinPresensiPegawaiService.getPegawaisByMesinPresensiAndTipePegawai(mesinPresensiSumber, tipePegawai.intValue());
        log.info("Size pegawaiIdListSumber {}", pegawaiIdListSumber.size());
        if (pegawaiIdListSumber.size() > 0) {
            // copy pegawai ke mesin presensi tujuan di model MesinPresensiPegawai
            mesinPresensiPegawaiService.copyToMesinPresensi(mesinPresensiTujuan, pegawaiIdListSumber);
            // sinkronisasi pegawai to mesin
            List<MesinPresensiPegawai> mesinPresensiPegawaiList = mesinPresensiPegawaiService.findAllByMesinPresensiAndTipePegawai(mesinPresensiTujuan, tipePegawai.intValue());
            log.info("Size mesinPresensiPegawaiList {}", mesinPresensiPegawaiList.size());
            sinkronisasiService.prosesSinkronisasiPegawaiToMesin(mesinPresensiPegawaiList);
        }

        ra.addFlashAttribute("messageOk", "Proses Copy Pegawai Selesai");
        log.info("==== Proses copy pegawai END ===");

        return "redirect:/pusat/mesin-presensi-copy-pegawai/list";
    }
}
