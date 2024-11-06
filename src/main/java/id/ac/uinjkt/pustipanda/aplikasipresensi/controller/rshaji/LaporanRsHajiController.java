package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.rshaji;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.KategoriPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.RekapPresensiPegawaiAdminUnitDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UnitKerjaPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.AbsensiUtils;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class LaporanRsHajiController {

//    @Autowired
//    private AbsensiDao absensiDao;

    @Autowired
    private PegawaiDao pegawaiDao;

    @Autowired
    private KategoriPegawaiDao kategoriPegawaiDao;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private AbsensiUtils absensiUtils;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @ModelAttribute
    public void addAttributes(Model mm) {
        Integer tahunNow = LocalDate.now().getYear();
        List<Integer> listTahun = IntStream.rangeClosed(tahunNow - 3, tahunNow)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        mm.addAttribute("listTahun", listTahun);
        mm.addAttribute("listBulan", AppConstant.BULAN);
        mm.addAttribute("listJenis", kategoriPegawaiDao.findAll());
    }


    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @GetMapping("/rshaji/laporan/rekap-absensi-csv")
    public String showRekapAbsensiCsv(ModelMap mm) {

        return "rshaji/laporan/rekap-absensi-csv";
    }

    @PreAuthorize("hasAnyAuthority('am','rshaji')")
    @PostMapping("/rshaji/laporan/rekap-absensi-csv")
    public void setRekapAbsensiCsv(ModelMap mm, @RequestParam(value = "tahun") Integer tahun,
                                   @RequestParam(value = "bulan") Integer bulan,
                                   @RequestParam(value = "tipe") String tipePegawai, HttpServletResponse response) throws IOException {
        if (tahun == null || tahun < 1) {
            mm.addAttribute("message", "Tahun tidak boleh kosong");
            //return "laporan/csv";
        }

        if (bulan == null || bulan < 1) {
            mm.addAttribute("message", "Bulan tidak boleh kosong");
            //return "laporan/csv";
        }

        if (tipePegawai == null) {
            mm.addAttribute("message", "Tipe pegawai tidak boleh kosong");
            //return "laporan/csv";
        }

        log.info("tahun: {}, bulan: {}", tahun, bulan);

        YearMonth date = YearMonth.of(tahun, bulan);
        LocalDate tglMulai = date.atDay(1);
        LocalDate tglSelesai = date.atEndOfMonth();
        String bulanTahun = date.getMonth().getDisplayName(TextStyle.FULL, new Locale("id", "ID")).toString().toLowerCase() + "-" + date.getYear();

        List<EmployeeDto> resultEmployeeDtos = new LinkedList<>();
        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiPegawaiAdminUnitDto = new LinkedList<>();

        //2 -> Semua Pegawai
        if (tipePegawai.equals("2")) {
            resultEmployeeDtos = pegawaiDao.findAllEmployeeByUnitKerjaPresensi(UnitKerjaPresensi.UNIT_KERJA_PRESENSI_RS_HAJI);

            for (EmployeeDto employeeDto : resultEmployeeDtos) {
                Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeDto.getId());
                RekapPresensiPegawaiAdminUnitDto data;
                if (pegawaiUtils.isPegawaiShift(pegawaiDto.get()))
                    data = absensiUtils.getRekapPresensiPerPegawaiShiftAU(pegawaiDto.get(), tglMulai, tglSelesai);
                else
                    data = absensiUtils.getRekapPresensiPerPegawaiAU(pegawaiDto.get(), tglMulai, tglSelesai);
                listRekapPresensiPegawaiAdminUnitDto.add(data);
            }

            if (listRekapPresensiPegawaiAdminUnitDto.size() > 0) {
                response.setContentType("text/csv");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rekap-absensi-rs-haji-" + bulanTahun + ".csv");
                response.getWriter().write("No, No Absen, Nama, NIP/NRP, Gol, Jabatan, Unit Kerja Presensi, Bagian, Juml. Hari Kerja, Jml. Hadir, DL, Cuti, Sakit, Tdk Absen Datang/Pulang, " +
                        "Alpa, Terlambat, Pulang Lebih Awal, Total Kekurangan Jam, Total Kekurangan Hari, Aktual Hari Kerja\n");
                int i = 1;
                for (RekapPresensiPegawaiAdminUnitDto a : listRekapPresensiPegawaiAdminUnitDto) {
                    log.info("no {}, no absensi {}, nama : {}", i, a.getNoAbsen(), a.getNama());
                    response.getWriter().write(
                            i
                                    + "," + (a.getNoAbsen() == null ? "" : a.getNoAbsen().replace("null", ""))
                                    + "," + a.getNama().replace(",", " ")
                                    + "," + (a.getNip() == null ? "" : a.getNip().replace("null", ""))
                                    + "," + (a.getGolongan() == null ? "" : a.getGolongan().replace(",", " "))
                                    + "," + a.getJabatan().replace(",", " ")
                                    + "," + a.getUnitKerjaPresensi()
                                    + "," + getUnitBagian(a.getIdPegawai())
                                    + "," + a.getHariKerja()
                                    + "," + a.getHadir()
                                    + "," + a.getDinasLuar()
                                    + "," + a.getCuti()
                                    + "," + a.getSakit()
                                    + "," + a.getTidakAbsen()
                                    + "," + a.getAlpa()
                                    + "," + currentService.getStringFromTime(a.getTimeTerlambat())
                                    + "," + currentService.getStringFromTime(a.getTimeKecepetan())
                                    + "," + currentService.getStringFromTime(a.getTimeJamKurang())
                                    + "," + a.getHariKurang()
                                    + "," + a.getAktualKerja().toString() + "\n"
                    );
                    i++;
                }
            }
        }
    }

    private String getJamKerjaString(Long input) {
        if (input == null)
            return "0:0";

        int tJam = currentService.getExactHourNumber(input.intValue());
        int tSisa = tJam / 3600;
        int tMenit = currentService.getExactMinuteNumber(tSisa);

        return tJam + ":" + tMenit;
    }

    private String getUnitBagian(Long idPegawai) {
        Optional<Pegawai> pegawai = pegawaiDao.findById(idPegawai);
        if (pegawai.isPresent()) {
            return pegawai.get().getSpesialisasi1();
        }

        return "";
    }
}
