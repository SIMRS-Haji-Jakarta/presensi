package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.HariKerjaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusHariDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.HariKerja;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
@RequestMapping("/pusat/hari-kerja")
public class HariKerjaController {
    @Autowired
    private StatusHariDao statusHariDao;

    @Autowired
    private JamKerjaDao jamKerjaDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @ModelAttribute
    public void addAttributes(Model mm) {
        Integer tahunNow = LocalDate.now().getYear();
        List<Integer> listTahun = IntStream.rangeClosed(tahunNow - 3, tahunNow + 1)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        mm.addAttribute("listTahun", listTahun);
        mm.addAttribute("listBulan", AppConstant.BULAN);
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/generate")
    public String showGenerate(ModelMap mm) {

        return "pusat/hari-kerja/generate";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/generate")
    public String setGenerate(ModelMap mm, @RequestParam(value = "tahun") Integer tahun, @RequestParam(value = "bulan") Integer bulan) {
        if (tahun == null || tahun < 1) {
            mm.addAttribute("message", "Tahun tidak boleh kosong");
            return "pusat/hari-kerja/generate";
        }

        if (bulan == null || bulan < 1) {
            mm.addAttribute("message", "Bulan tidak boleh kosong");
            return "pusat/hari-kerja/generate";
        }

        log.info("tahun: {}, bulan: {}", tahun, bulan);
        YearMonth yearMonth = YearMonth.of(tahun, bulan);
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate tanggal = LocalDate.of(tahun, bulan, i);
            //LOGGER.info("tanggal: {}", tanggal.getDayOfWeek());

            Optional<HariKerja> hariKerjaO = hariKerjaDao.findByTanggal(tanggal);
            if (hariKerjaO.isPresent()) {
                log.info("sudah tanggal: {}", tanggal);
            } else {
                log.info("belum tanggal: {}", tanggal);

                HariKerja hk = new HariKerja();
                hk.setTanggal(tanggal);
                if (tanggal.getDayOfWeek().toString().equalsIgnoreCase("FRIDAY")) {
                    hk.setJamKerja(AppConstant.JAM_NORMAL_JUMAT);
                } else {
                    hk.setJamKerja(AppConstant.JAM_NORMAL);
                }
                if (tanggal.getDayOfWeek().toString().equalsIgnoreCase("SATURDAY") || tanggal.getDayOfWeek().toString().equalsIgnoreCase("SUNDAY")) {
                    hk.setStatusHari(AppConstant.HARI_LIBUR);
                } else {
                    hk.setStatusHari(AppConstant.HARI_MASUK);
                }
//                LOGGER.info("hk: {}", hk);
                hariKerjaDao.save(hk);
            }
        }


        //return "redirect:/pusat/hari-kerja/generate";
        return "redirect:/pusat/hari-kerja/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm, @RequestParam(value = "tahun", required = false) Integer tahun, @RequestParam(value = "bulan", required = false) Integer bulan, @PageableDefault(size = 10) Pageable page) {
        Page<HariKerja> result;

        if (tahun != null && bulan != null) {
            result = hariKerjaDao.findByTahunBulan(tahun, bulan, page);
            mm.addAttribute("tahun", tahun);
            mm.addAttribute("bulan", bulan);
        } else {
            result = hariKerjaDao.findAll(page);
        }
        mm.addAttribute("data", result);

        return "pusat/hari-kerja/list";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String tanggal, ModelMap mm) {
        log.info("tanggal: {}", tanggal);

        HariKerja hariKerja = new HariKerja();
        if (StringUtils.hasText(tanggal)) {
            //   if (tanggal != null) {
            Optional<HariKerja> o = hariKerjaDao.findByTanggal(LocalDate.parse(tanggal));
            if (o.isPresent()) {
                hariKerja = o.get();
            }
        }

        mm.addAttribute("listStatusHari", statusHariDao.findAll());
        mm.addAttribute("listJamKerja", jamKerjaDao.findAll());
        mm.addAttribute("hariKerja", hariKerja);

        return "pusat/hari-kerja/form";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid HariKerja o, BindingResult errors, ModelMap mm) {
        log.info("error : {}", errors);
        if (errors.hasErrors()) {
            mm.addAttribute("listStatusHari", statusHariDao.findAll());
            mm.addAttribute("listJamKerja", jamKerjaDao.findAll());
            mm.addAttribute("hariKerja", o);

            return "pusat/hari-kerja/form";
        } else {
            hariKerjaDao.save(o);

            return "redirect:/pusat/hari-kerja/list";
        }
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    @Transactional
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggal(LocalDate.parse(id));
        if (hariKerja.isPresent()) {
            hariKerjaDao.deleteByTanggal(LocalDate.parse(id));
        } else {
            ra.addFlashAttribute("message", "Data tidak ditemukan");
        }

        return "redirect:/pusat/hari-kerja/list";
    }

}
