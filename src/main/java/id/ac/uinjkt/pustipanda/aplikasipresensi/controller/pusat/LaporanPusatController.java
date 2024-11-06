package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.AbsensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.PegawaiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.AbsensiUtils;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
public class LaporanPusatController {
    @Autowired
    private UnitDao unitDao;

//    @Autowired
//    private AbsensiDao absensiDao;

    @Autowired
    private PegawaiDao pegawaiDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @Autowired
    private KategoriPegawaiDao kategoriPegawaiDao;

    @Autowired
    private StatusPresensiDao statusPresensiDao;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private AbsensiUtils absensiUtils;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @Autowired
    private AbsensiService absensiService;

    @Autowired
    private PegawaiService pegawaiService;

    @ModelAttribute
    public void addAttributes(Model mm) {
        Integer tahunNow = LocalDate.now().getYear();
        List<Integer> listTahun = IntStream.rangeClosed(tahunNow - 3, tahunNow)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        mm.addAttribute("listTahun", listTahun);
        mm.addAttribute("listBulan", AppConstant.BULAN);
        mm.addAttribute("listUnit", unitDao.findAll(Sort.by("nama")));
        mm.addAttribute("listJenis", kategoriPegawaiDao.findAll());
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/csv")
    public String showCsv(ModelMap mm) {

        return "pusat/laporan/csv";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/csv")
    public void setCsv(ModelMap mm, @RequestParam(value = "tahun") Integer tahun, @RequestParam(value = "bulan") Integer bulan, HttpServletResponse response) throws IOException {
        if (tahun == null || tahun < 1) {
            mm.addAttribute("message", "Tahun tidak boleh kosong");
            //return "laporan/csv";
        }

        if (bulan == null || bulan < 1) {
            mm.addAttribute("message", "Bulan tidak boleh kosong");
            //return "laporan/csv";
        }

        log.info("tahun: {}, bulan: {}", tahun, bulan);

        List<EmployeeDto> resultEmployeeDtos = pegawaiDao.findAllEmployee();
        List<RekapAbsensiCsvPegawaiDto> listRekapAbsensiCsvPegawaiDto = new LinkedList<>();
        for (EmployeeDto employeeDto : resultEmployeeDtos) {
            Optional<Pegawai> pegawai = pegawaiDao.findById(employeeDto.getId());
            List<RekapAbsensiCsvPegawaiDto> data = new LinkedList<>();
            if (pegawai.isPresent()) {
                data = absensiUtils.getRekapAbsensiCsvPerPegawai(pegawai.get(), tahun, bulan);
            }
            listRekapAbsensiCsvPegawaiDto.addAll(data);
        }

        //sort list by tanggal
        if (listRekapAbsensiCsvPegawaiDto.size() > 0)
            Collections.sort(listRekapAbsensiCsvPegawaiDto);

        if (listRekapAbsensiCsvPegawaiDto.size() > 0) {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=absensi.csv");
            response.getWriter().write("No Absen, Nama, Unit Kerja Presensi, Tanggal, In, Out, Jabatan, Keterangan\n");
            for (RekapAbsensiCsvPegawaiDto a : listRekapAbsensiCsvPegawaiDto) {
                log.info("absensi, nama : {}, tanggal: {}, masuk: {}, pulang: {}", a.getNama(), a.getTanggal(), a.getWaktuIn(), a.getWaktuOut());
                response.getWriter().write(
                        a.getNomorAbsen() == null ? "" : a.getNomorAbsen().replace("null", "")
                                + "," + a.getNama().replace(",", " ") + "," + a.getUnitKerjaPresensi().replace(",", " ")
                                + "," + a.getTanggal() + "," + String.valueOf(a.getWaktuIn()).replace("null", "") + "," + String.valueOf(a.getWaktuOut()).replace("null", "")
                                + "," + a.getJabatan().replace(",", " ")
                                + "," + a.getKeterangan() + "\n"
                );
            }
        }

/*        Set<Absensi> absensis = absensiDao.findAllAbsensiByTahunBulan(tahun, bulan);
        if (absensis.size() > 0) {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=absensi.csv");
            response.getWriter().write("No Absen, Nama, Unit Kerja Presensi, Tanggal, In, Out, Jabatan, Keterangan\n");
            for (Absensi a : absensis) {
                log.info("absensi, nama : {}, tanggal: {}, masuk: {}, pulang: {}", Pegawai.toStringNama(a.getPegawai()), a.getTanggal(), a.getWaktuIn(), a.getWaktuOut());
                response.getWriter().write(
                        a.getPegawai().getNomorAbsen() == null ? "" : a.getPegawai().getNomorAbsen()
                                + "," + Pegawai.toStringNama(a.getPegawai()).replace(",", " ") + "," + (a.getPegawai().getUnitKerjaPresensi() == null ? "" : a.getPegawai().getUnitKerjaPresensi().getNama()).replace(",", " ")
                                + "," + a.getTanggal() + "," + String.valueOf(a.getWaktuIn()).replace("null", "") + "," + String.valueOf(a.getWaktuOut()).replace("null", "")
                                + "," + String.valueOf(a.getPegawai().getJabatanPegawai() == null ? null : a.getPegawai().getJabatanPegawai().getNama()).replace("null", "")
                                + "," + String.valueOf(a.getStatusPresensi() == null ? null : a.getStatusPresensi().getNama()).replace("null", "") + "\n"
                );
            }
        }*/
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/rekap-absensi-csv")
    public String showRekapAbsensiCsv(ModelMap mm) {

        return "pusat/laporan/rekap-absensi-csv";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/rekap-absensi-csv")
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
            resultEmployeeDtos = pegawaiDao.findAllEmployeeSortByUnitKerja();

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
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rekap-absensi-" + bulanTahun + ".csv");
                response.getWriter().write("No, No Absen, Nama, NIP/NRP, Gol, Jabatan, Unit Kerja Presensi, Juml. Hari Kerja, Jml. Hadir, DL, Cuti, Sakit, Tdk Absen Datang/Pulang, " +
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
        //1 -> Dosen
        else if (tipePegawai.equals("1")) {
            resultEmployeeDtos = pegawaiDao.findAllEmployeeDosenSortByUnitKerja();

            for (EmployeeDto employeeDto : resultEmployeeDtos) {
                Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeDto.getId());
                RekapPresensiPegawaiAdminUnitDto data = absensiUtils.getRekapPresensiPerDosenAU(pegawaiDto.get(), tglMulai, tglSelesai);
                listRekapPresensiPegawaiAdminUnitDto.add(data);
            }

            if (listRekapPresensiPegawaiAdminUnitDto.size() > 0) {
                response.setContentType("text/csv");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rekap-absensi-dosen-" + bulanTahun + ".csv");
                response.getWriter().write("No, No Absen, Nama, NIP, Gol, Jabatan, Unit Kerja Presensi, Jumlah Hadir, Total Jam Kerja\n");
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
                                    + "," + (a.getUnitKerjaPresensi())
                                    + "," + a.getHadir().toString()
                                    + "," + getJamKerjaString(a.getJamKerja()) + "\n"
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

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/absensi")
    public String showAbsensi(ModelMap mm, @RequestParam(value = "tanggal", required = false) String tanggal,
                              @RequestParam(value = "kategori", required = false) KategoriPegawai kategori,
                              @RequestParam(value = "unit", required = false) Unit unit,
                              @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari, @PageableDefault(value = 10) Pageable page) {

        LocalDate oTanggal = (tanggal == null ? LocalDate.now() : LocalDate.parse(tanggal));
        Page<Absensi> absensiList = absensiService.getPageFilterByTanggalAndPegawaiOrUnitOrKategori(oTanggal, pegawaiCari, unit, kategori, page);

        mm.addAttribute("tanggal", oTanggal);
        mm.addAttribute("kategori", kategori);
        mm.addAttribute("unit", unit);
        mm.addAttribute("pegawaiCari", pegawaiCari);
        mm.addAttribute("data", absensiList);

        return "pusat/laporan/absensi";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/inout")
    public String showInOut() {
        return "pusat/laporan/inout";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/inout-print-all")
    public String printIntOut(ModelMap mm,
                              @RequestParam(value = "mulai", required = false) String mulai,
                              @RequestParam(value = "selesai", required = false) String selesai,
                              @RequestParam(value = "unit") Long unit,
                              @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                              @RequestParam(value = "kategori") Long kategori,
                              @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        Optional<Unit> oUnit;
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = new LinkedList<>();

        if (pegawaiCari != null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiShift(pegawaiCari.getId());
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawai(pegawaiCari.getId());
        }

        if (pegawaiCari != null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawaiShift(pegawaiCari.getId(), kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawai(pegawaiCari.getId(), kategori);
        }

        if (unit == 0 && pegawaiCari == null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawaiShift(kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawai(kategori);
        }
        if (unit != 0 && pegawaiCari == null && kategori == 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitByPegawaiShift(unit);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnit(unit);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit != 0 && pegawaiCari == null && kategori != 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawaiByPegawaiShift(unit, kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawai(unit, kategori);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit == 0 && pegawaiCari == null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAllByPegawaiShift();
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAll();
        }

        List<InOutAdminUnitDto> listInOutAdminUnit = new LinkedList<>();
        List<InOutAdminUnitDto> resultInOutAdminUnit;

        int j = 0;
        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            resultEmployeeAdminUnit.get(j).setNama(Pegawai.toStringNama(pegawaiDto.get()));
            j++;
        }

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            resultInOutAdminUnit = hariKerjaDao.findAllByPegawaiAndPeriode(pegawaiDto.get(), tglMulai, tglSelesai);

            int i = 0;
            for (InOutAdminUnitDto inOutAdminUnitDto : resultInOutAdminUnit) {
                if (inOutAdminUnitDto.getPegawai() == null) {
                    resultInOutAdminUnit.get(i).setPegawai(pegawaiDto.get());
                }
                i++;
            }
            listInOutAdminUnit.addAll(resultInOutAdminUnit);
        }

        //Pejabat Kepala Biro AUK
        Pegawai pejabat = pegawaiService.getPejabatKepalaBiroAUK();

        Map<String, Integer> listBulan = absensiUtils.getListBulan(tglMulai, tglSelesai);

        mm.addAttribute("listBulan", listBulan);
        mm.addAttribute("hariKerja", hariKerjaDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(tglMulai, tglSelesai));
        mm.addAttribute("data", resultEmployeeAdminUnit);
        mm.addAttribute("dataInOut", listInOutAdminUnit);

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("pejabat", pejabat);

        return "pusat/laporan/inout-print-all";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/rekappegawai")
    public String showRekapPegawaiAdminPusat() {
        return "pusat/laporan/rekappegawai";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/rekappegawai-print-all")
    public String printRekapPegawaiAdminPusat(ModelMap mm,
                                              @RequestParam(value = "mulai", required = false) String mulai,
                                              @RequestParam(value = "selesai", required = false) String selesai,
                                              @RequestParam(value = "unit") Long unit,
                                              @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                              @RequestParam(value = "kategori") Long kategori,
                                              @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        Optional<Unit> oUnit;

        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = new LinkedList<>();

        if (pegawaiCari != null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiShift(pegawaiCari.getId());
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawai(pegawaiCari.getId());
        }

        if (pegawaiCari != null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawaiShift(pegawaiCari.getId(), kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawai(pegawaiCari.getId(), kategori);
        }

        if (unit == 0 && pegawaiCari == null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawaiShift(kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawai(kategori);
        }
        if (unit != 0 && pegawaiCari == null && kategori == 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitByPegawaiShift(unit);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnit(unit);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit != 0 && pegawaiCari == null && kategori != 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawaiByPegawaiShift(unit, kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawai(unit, kategori);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit == 0 && pegawaiCari == null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAllByPegawaiShift();
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAll();
        }

        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiPegawaiAdminUnitDto = new LinkedList<>();

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            RekapPresensiPegawaiAdminUnitDto data;
            if (pegawaiUtils.isPegawaiShift(pegawaiDto.get()))
                data = absensiUtils.getRekapPresensiPerPegawaiShiftAU(pegawaiDto.get(), tglMulai, tglSelesai);
            else
                data = absensiUtils.getRekapPresensiPerPegawaiAU(pegawaiDto.get(), tglMulai, tglSelesai);
            listRekapPresensiPegawaiAdminUnitDto.add(data);
        }

        //Pejabat Kepala Biro AUK
        Pegawai pejabat = pegawaiService.getPejabatKepalaBiroAUK();

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", listRekapPresensiPegawaiAdminUnitDto);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("currentService", currentService);

        return "pusat/laporan/rekappegawai-print-all";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/rekapdosen")
    public String showRekapDosenAdminPusat() {
        return "pusat/laporan/rekapdosen";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/rekapdosen-print-all")
    public String printRekapDosenAdminPusat(ModelMap mm,
                                            @RequestParam(value = "mulai", required = false) String mulai,
                                            @RequestParam(value = "selesai", required = false) String selesai,
                                            @RequestParam(value = "unit") Long unit,
                                            @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                            @RequestParam(value = "kategori") Long kategori) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        Optional<Unit> oUnit;

        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = new LinkedList<>();

        if (pegawaiCari != null && kategori == 0) {
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminPusatByPegawai(pegawaiCari.getId());
        }

        if (pegawaiCari != null && kategori != 0) {
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminPusatByPegawaiAndKategoriPegawai(pegawaiCari.getId(), kategori);
        }

        if (unit == 0 && pegawaiCari == null && kategori != 0) {
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByKategoriPegawai(kategori);
        }
        if (unit != 0 && pegawaiCari == null && kategori == 0) {
            oUnit = unitDao.findById(unit);
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByUnit(unit);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit != 0 && pegawaiCari == null && kategori != 0) {
            oUnit = unitDao.findById(unit);
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByUnitAndKategoriPegawai(unit, kategori);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit == 0 && pegawaiCari == null && kategori == 0) {
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByAll();
        }

        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiDosenAdminUnitDto = new LinkedList<>();

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            RekapPresensiPegawaiAdminUnitDto data = absensiUtils.getRekapPresensiPerDosenAU(pegawaiDto.get(), tglMulai, tglSelesai);
            listRekapPresensiDosenAdminUnitDto.add(data);
        }

        //Pejabat Kepala Biro AUK
        Pegawai pejabat = pegawaiService.getPejabatKepalaBiroAUK();

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", listRekapPresensiDosenAdminUnitDto);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("currentService", currentService);

        return "pusat/laporan/rekapdosen-print-all";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/harian")
    public String showRekapHarian() {

        return "pusat/laporan/harian";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/harian-print-all")
    public String printRekapHarian(ModelMap mm, @RequestParam(value = "kategori") Long kategori) {

        LocalDate tglSekarang = LocalDate.now();

        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = new LinkedList<>();
        Optional<KategoriPegawai> kategoriPegawai = kategoriPegawaiDao.findById(kategori);

        if (kategori != 0)
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawai(kategori);
        else
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAll();

        List<RekapHarianPegawaiDto> listRekapHarianPegawaiDto = new LinkedList<>();

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            RekapHarianPegawaiDto data = absensiUtils.getRekapHarianAll(pegawaiDto.get(), tglSekarang);
            listRekapHarianPegawaiDto.add(data);
        }

        //Pejabat REKTOR
        Long idPegawai = pegawaiDao.findIdRektor();
        Optional<Pegawai> pejabat = pegawaiDao.findById(idPegawai);
        if (pejabat.isPresent()) {
            pejabat.get().setNama(Pegawai.toStringNama(pejabat.get()));
            pejabat.get().getJabatanPegawai().setNama("PIMPINAN SATKER");
        } else
            pejabat = Optional.of(new Pegawai());

        kategoriPegawai.ifPresent(pegawai -> mm.addAttribute("kategoriPegawai", pegawai));
        mm.addAttribute("tglSekarang", tglSekarang);
        mm.addAttribute("data", listRekapHarianPegawaiDto);
        mm.addAttribute("pejabat", pejabat.get());

        return "pusat/laporan/harian-print-all";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @GetMapping("/pusat/laporan/rekapketidakhadiran")
    public String showRekapKetidakhadiranAdminPusat(ModelMap mm) {

        List<StatusPresensi> listStatusPresensi = statusPresensiDao.findAllStatusByFilter();

        mm.addAttribute("listStatusPresensi", listStatusPresensi);
        return "pusat/laporan/rekapketidakhadiran";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst','remun')")
    @PostMapping("/pusat/laporan/rekapketidakhadiran-print-all")
    public String printRekapKetidakhadiranAdminPusat(ModelMap mm,
                                                     @RequestParam(value = "mulai", required = false) String mulai,
                                                     @RequestParam(value = "selesai", required = false) String selesai,
                                                     @RequestParam(value = "unit") Long unit,
                                                     @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                                     @RequestParam(value = "kategori") Long kategori,
                                                     @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift,
                                                     @RequestParam(value = "statusPresensi") String idStatusPresensi) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        StatusPresensi statusPresensi = null;
        if (!idStatusPresensi.isEmpty()) {
            statusPresensi = statusPresensiDao.getOne(idStatusPresensi);
        }

        Optional<Unit> oUnit;
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = new LinkedList<>();

        if (pegawaiCari != null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiShift(pegawaiCari.getId());
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawai(pegawaiCari.getId());
        }

        if (pegawaiCari != null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawaiShift(pegawaiCari.getId(), kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminPusatByPegawaiAndKategoriPegawai(pegawaiCari.getId(), kategori);
        }

        if (unit == 0 && pegawaiCari == null && kategori != 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawaiShift(kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByKategoriPegawai(kategori);
        }
        if (unit != 0 && pegawaiCari == null && kategori == 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitByPegawaiShift(unit);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnit(unit);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit != 0 && pegawaiCari == null && kategori != 0) {
            oUnit = unitDao.findById(unit);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawaiByPegawaiShift(unit, kategori);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByUnitAndKategoriPegawai(unit, kategori);

            mm.addAttribute("unit", oUnit.get());
        }
        if (unit == 0 && pegawaiCari == null && kategori == 0) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAllByPegawaiShift();
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByAll();
        }

        List<RekapKetidakhadiranPegawaiAdminUnitDto> listRekapKetidakhadiranPegawaiAdminUnitDto = new LinkedList<>();

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            List<RekapKetidakhadiranPegawaiAdminUnitDto> data;
            if (pegawaiDto.isPresent()) {
                if (pegawaiUtils.isPegawaiShift(pegawaiDto.get()))
                    data = absensiUtils.getRekapKetidakhadiranPerPegawaiShiftAU(pegawaiDto.get(), tglMulai, tglSelesai, statusPresensi);
                else
                    data = absensiUtils.getRekapKetidakhadiranPerPegawaiAU(pegawaiDto.get(), tglMulai, tglSelesai, statusPresensi);
                listRekapKetidakhadiranPegawaiAdminUnitDto.addAll(data);
            }
        }

        //sort list by tanggal
        if (listRekapKetidakhadiranPegawaiAdminUnitDto.size() != 0)
            Collections.sort(listRekapKetidakhadiranPegawaiAdminUnitDto);

        //Pejabat Kepala Biro AUK
        Pegawai pejabat = pegawaiService.getPejabatKepalaBiroAUK();

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", listRekapKetidakhadiranPegawaiAdminUnitDto);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("currentService", currentService);

        return "pusat/laporan/rekapketidakhadiran-print-all";
    }
}
