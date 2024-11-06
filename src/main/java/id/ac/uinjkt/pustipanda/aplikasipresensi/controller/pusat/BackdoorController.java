package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AbsensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Absensi;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/pusat/backdoor")
public class BackdoorController {
    @Autowired
    private AbsensiDao absensiDao;

    @Autowired
    private PegawaiDao pegawaiDao;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "nama", required = false) String nama, @RequestParam(value = "tanggal", required = false) String tanggal, @PageableDefault(size = 10) Pageable page) {
        Page<Absensi> result;

        log.info("nama: {}, tanggal: {}", nama, tanggal);

        if (nama == null && tanggal == null) {
            result = absensiDao.findAllByOrderByTanggalDesc(page);
        } else {
            if (tanggal == null || tanggal.isEmpty()) {
                result = absensiDao.findAllByPegawaiNamaContainingIgnoreCaseOrderByTanggalDesc(nama, page);
                mm.addAttribute("nama", nama);
            } else {
                //result = absensiDao.findAllByTanggal(LocalDate.parse(tanggal), page);
                result = absensiDao.findAllByTanggalAndPegawaiNamaContainingIgnoreCase(LocalDate.parse(tanggal), nama, page);
                mm.addAttribute("tanggal", tanggal);
                mm.addAttribute("nama", nama);
            }
        }

        mm.addAttribute("absensi", new Absensi());
        mm.addAttribute("data", result);
        return "pusat/backdoor/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@RequestParam(value = "fpegawai", required = false) String fpegawai,
                             @RequestParam(value = "ftanggal", required = false) String ftanggal,
                             @RequestParam(value = "fjamdatang", required = false) String fjamdatang,
                             @RequestParam(value = "fjampulang", required = false) String fjampulang,
                             ModelMap mm, RedirectAttributes ra) {
        log.info("pegawai: {}, tanggal: {}, datang: {}, pulang : {}", fpegawai, ftanggal, fjamdatang, fjampulang);

        if (fpegawai == null) {
            ra.addFlashAttribute("message", "Pegawai tidak boleh kosong.");
            return "redirect:/pusat/backdoor/list";
        }

        if (ftanggal.isEmpty()) {
            ra.addFlashAttribute("message", "Tanggal tidak boleh kosong.");
            return "redirect:/pusat/backdoor/list";
        }

        if (fjamdatang.isEmpty()) {
            ra.addFlashAttribute("message", "Jam Datang tidak boleh kosong.");
            return "redirect:/pusat/backdoor/list";
        }

        if (fjampulang.isEmpty()) {
            ra.addFlashAttribute("message", "Jam Pulang tidak boleh kosong.");
            return "redirect:/pusat/backdoor/list";
        }

        Optional<Pegawai> oPegawai = pegawaiDao.findById(Long.parseLong(fpegawai));
        if (oPegawai.isPresent()) {
            Pegawai pegawai = oPegawai.get();
            Absensi absensi = new Absensi();
            absensi.setPegawai(pegawai);
            absensi.setTanggal(LocalDate.parse(ftanggal));
            absensi.setWaktuIn(LocalTime.parse(fjamdatang));
            absensi.setWaktuInIp(AppConstant.DEFAULT_IP);
            absensi.setWaktuInLat(AppConstant.DEFAULT_LATITUDE);
            absensi.setWaktuInLgn(AppConstant.DEFAULT_LONGITUDE);
            absensi.setWaktuOut(LocalTime.parse(fjampulang));
            absensi.setWaktuOutIp(AppConstant.DEFAULT_IP);
            absensi.setWaktuOutLat(AppConstant.DEFAULT_LATITUDE);
            absensi.setWaktuOutLgn(AppConstant.DEFAULT_LONGITUDE);
            absensi.setStatusPresensi(AppConstant.STATUS_HADIR);
            log.info("absensi: {} ", absensi);

            absensiDao.save(absensi);

        } else {
            ra.addFlashAttribute("message", "Pegawai tidak ditemukan.");
            return "redirect:/pusat/backdoor/list";
        }

        return "redirect:/pusat/backdoor/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/absensi/{id}")
    public String showAbsensi(ModelMap mm, @PathVariable(value = "id", required = false) String id) {
        log.info("absensi: {}", id);
        Absensi absensi = absensiDao.getOne(id);
        mm.addAttribute("a", absensi);

        return "pusat/backdoor/list :: modalEditPresensi";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/update")
    public String updateAbsensi(@ModelAttribute @Valid Absensi o, BindingResult errors, ModelMap mm) {
        log.info("datang: {}, pulang: {}", o.getWaktuIn(), o.getWaktuOut());

        Absensi absensi = absensiDao.getOne(o.getId());
        absensi.setWaktuIn(o.getWaktuIn());
        absensi.setWaktuInIp(absensi.getWaktuInIp() == null ? AppConstant.DEFAULT_IP : absensi.getWaktuInIp());
        absensi.setWaktuInLat(absensi.getWaktuInLat() == null ? AppConstant.DEFAULT_LATITUDE : absensi.getWaktuInLat());
        absensi.setWaktuInLgn(absensi.getWaktuInLgn() == null ? AppConstant.DEFAULT_LONGITUDE : absensi.getWaktuOutLgn());
        absensi.setWaktuOut(o.getWaktuOut());
        absensi.setWaktuOutIp(absensi.getWaktuOutIp() == null ? AppConstant.DEFAULT_IP : absensi.getWaktuOutIp());
        absensi.setWaktuOutLat(absensi.getWaktuOutLat() == null ? AppConstant.DEFAULT_LATITUDE : absensi.getWaktuOutLat());
        absensi.setWaktuOutLgn(absensi.getWaktuOutLgn() == null ? AppConstant.DEFAULT_LONGITUDE : absensi.getWaktuOutLgn());
        absensi.setStatusPresensi(AppConstant.STATUS_HADIR);

        log.info("absensi: {}", absensi);
        absensiDao.save(absensi);


        return "redirect:/pusat/backdoor/list";
    }
}
