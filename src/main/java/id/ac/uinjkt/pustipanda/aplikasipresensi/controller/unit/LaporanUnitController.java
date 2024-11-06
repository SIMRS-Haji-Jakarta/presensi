package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.unit;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JenisPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.AbsensiUtils;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class LaporanUnitController {
    @Autowired
    private PegawaiDao pegawaiDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @Autowired
    private JenisPegawaiDao jenisPegawaiDao;

    @Autowired
    private KategoriPegawaiDao kategoriPegawaiDao;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private AbsensiUtils absensiUtils;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @Autowired
    private PegawaiShiftDao pegawaiShiftDao;

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

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/laporan/in-out")
    public String getInOut(ModelMap mm, Authentication auth,
                           @RequestParam(value = "mulai", required = false) String mulai,
                           @RequestParam(value = "selesai", required = false) String selesai,
                           @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                           @RequestParam(value = "idJenisPegawai", required = false) Long idJenisPegawai,
                           @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);
        List<EmployeeDto> listPegawai = pegawaiDao.findAllEmployeeByListUnitKerja(listIdUnitKerja);

        List<InOutDto> result = null;
        List<InOutAdminUnitDto> resultInOutAdminUnit = null;
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;
        String jenisReport = null;
        JenisPegawai jenisPegawai = null;

/*
        jenisReport -> Type1, untuk view pegawai saja atau dengan jenis pegawai
        jenisReport -> Type2, untuk view daftar pegawai sesuai jenis pegawai yang dipilih
* */

        if (pegawaiCari != null && idJenisPegawai == null) {
            if (isPegawaiShift != null) {
                if (pegawaiUtils.isPegawaiShift(pegawaiCari))
                    result = hariKerjaDao.findAllByPeriode(pegawaiCari, tglMulai, tglSelesai);
            } else
                result = hariKerjaDao.findAllByPeriode(pegawaiCari, tglMulai, tglSelesai);

            jenisReport = "Type1";

            mm.addAttribute("data", result);
        }
        if (pegawaiCari != null && idJenisPegawai != null) {
            jenisPegawai = jenisPegawaiDao.getOne(idJenisPegawai);
            if (isPegawaiShift != null) {
                if (pegawaiUtils.isPegawaiShift(pegawaiCari))
                    result = hariKerjaDao.findAllByPeriodeAndJenisPegawai(pegawaiCari, jenisPegawai, tglMulai, tglSelesai);
            } else
                result = hariKerjaDao.findAllByPeriodeAndJenisPegawai(pegawaiCari, jenisPegawai, tglMulai, tglSelesai);

            jenisReport = "Type1";

            mm.addAttribute("data", result);
        }
        if (pegawaiCari == null && idJenisPegawai != null) {
            jenisPegawai = jenisPegawaiDao.getOne(idJenisPegawai);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawaiByPegawaiShift(listIdUnitKerja, idJenisPegawai);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawai(listIdUnitKerja, idJenisPegawai);
            List<InOutAdminUnitDto> listInOutAdminUnit = new LinkedList<>();

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

            jenisReport = "Type2";

            Map<String, Integer> listBulan = absensiUtils.getListBulan(tglMulai, tglSelesai);

            mm.addAttribute("listBulan", listBulan);
            mm.addAttribute("hariKerja", hariKerjaDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(tglMulai, tglSelesai));
            mm.addAttribute("data", resultEmployeeAdminUnit);
            mm.addAttribute("dataInOut", listInOutAdminUnit);
        }
        if (mulai != null && selesai != null && pegawaiCari == null && idJenisPegawai == null) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitByPegawaiShift(listIdUnitKerja);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnit(listIdUnitKerja);
            List<InOutAdminUnitDto> listInOutAdminUnit = new LinkedList<>();

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

            jenisReport = "Type2";

            Map<String, Integer> listBulan = absensiUtils.getListBulan(tglMulai, tglSelesai);

            mm.addAttribute("listBulan", listBulan);
            mm.addAttribute("hariKerja", hariKerjaDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(tglMulai, tglSelesai));
            mm.addAttribute("data", resultEmployeeAdminUnit);
            mm.addAttribute("dataInOut", listInOutAdminUnit);
        }

        mm.addAttribute("listIdUnitKerja", listIdUnitKerja);
        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("pegawaiCari", pegawaiCari);
        mm.addAttribute("listJenisPegawai", jenisPegawaiDao.findAll());
        mm.addAttribute("jenisReport", jenisReport);
        mm.addAttribute("jenisPegawai", jenisPegawai);
        mm.addAttribute("listPegawai", listPegawai);
        mm.addAttribute("idJenisPegawai", idJenisPegawai);
        mm.addAttribute("isPegawaiShift", isPegawaiShift);

        return "unit/laporan/in-out";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/in-out-print")
    public String printInOut(ModelMap mm, Authentication auth,
                             @RequestParam(value = "mulai", required = false) String mulai,
                             @RequestParam(value = "selesai", required = false) String selesai,
                             @RequestParam(value = "idPegawaiCari", required = false) Long idPegawaiCari,
                             @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        Pegawai pegawaiCari = pegawaiDao.getOne(idPegawaiCari);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawaiCari);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<InOutDto> result = null;

        // LOGGER.info("mulai: {}, selesai: {}, unit: {}", tglMulai, tglSelesai, adminUnit);
        if (isPegawaiShift != null) {
            if (pegawaiUtils.isPegawaiShift(pegawaiCari))
                result = hariKerjaDao.findAllByPeriode(pegawaiCari, tglMulai, tglSelesai);
        } else
            result = hariKerjaDao.findAllByPeriode(pegawaiCari, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawaiCari));
        mm.addAttribute("pegawai", pegawaiCari);
        mm.addAttribute("data", result);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pejabat", pejabat);

        return "unit/laporan/in-out-print";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/in-out-print-all")
    public String printInOutAll(ModelMap mm, Authentication auth,
                                @RequestParam(value = "mulai", required = false) String mulai,
                                @RequestParam(value = "selesai", required = false) String selesai,
                                @RequestParam(value = "idJenisPegawai", required = false) Long idJenisPegawai,
                                @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtdByPegawaiAU(pegawai);

        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);

        List<InOutAdminUnitDto> resultInOutAdminUnit = null;
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;
        JenisPegawai jenisPegawai = null;

        if (idJenisPegawai != null) {
            jenisPegawai = jenisPegawaiDao.getOne(idJenisPegawai);
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawaiByPegawaiShift(listIdUnitKerja, idJenisPegawai);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawai(listIdUnitKerja, idJenisPegawai);
        } else {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitByPegawaiShift(listIdUnitKerja);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnit(listIdUnitKerja);
        }

        List<InOutAdminUnitDto> listInOutAdminUnit = new LinkedList<>();

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

        //Pejabat
        //Pegawai pejabat = pegawaiDao.findAllByUnitKerjaIdAndJabatanPegawaiIdOrderById(333L, 52L);
        Pegawai pejabat = adminUnit.getPjPegawai();
        if (pejabat != null) {
            pejabat.setNama(Pegawai.toStringNama(pejabat));
            pejabat.getJabatanPegawai().setNama(adminUnit.getPjJabatan() == null ? "" : adminUnit.getPjJabatan());
        } else
            pejabat = new Pegawai();

        Map<String, Integer> listBulan = absensiUtils.getListBulan(tglMulai, tglSelesai);

        mm.addAttribute("listBulan", listBulan);
        mm.addAttribute("hariKerja", hariKerjaDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(tglMulai, tglSelesai));
        mm.addAttribute("data", resultEmployeeAdminUnit);
        mm.addAttribute("dataInOut", listInOutAdminUnit);

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("jenisPegawai", jenisPegawai);
        mm.addAttribute("pejabat", pejabat);

        return "unit/laporan/in-out-print-all";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/laporan/rekap-pegawai")
    public String getRekapPegawai(ModelMap mm, Authentication auth,
                                  @RequestParam(value = "mulai", required = false) String mulai,
                                  @RequestParam(value = "selesai", required = false) String selesai,
                                  @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                  @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai,
                                  @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);
        List<EmployeeDto> listPegawai = pegawaiDao.findAllEmployeeByListUnitKerja(listIdUnitKerja);

        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;
        String jenisReport = null;
        List<RekapPresensiPegawaiDto> listRekapPresensiPegawaiDto = null;
        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiPegawaiAdminUnitDto = new LinkedList<>();

        if (pegawaiCari != null && idKategoriPegawai == null) {
            Pegawai oPegawai = null;
            jenisReport = "Type1";
            if (isPegawaiShift != null) {
                Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findFirstByPegawai(pegawaiCari);
                if (pegawaiShift.isPresent())
                    oPegawai = pegawaiShift.get().getPegawai();
            } else
                oPegawai = pegawaiCari;

            if (oPegawai != null) {
                if (pegawaiUtils.isPegawaiShift(oPegawai))
                    listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawaiShift(oPegawai, tglMulai, tglSelesai);
                else
                    listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawai(oPegawai, tglMulai, tglSelesai);
            } else
                jenisReport = null;

            mm.addAttribute("data", listRekapPresensiPegawaiDto);
        }

        if (pegawaiCari != null && idKategoriPegawai != null) {
            Long idPegawai = pegawaiCari.getId();

            Pegawai oPegawai;
            jenisReport = "Type1";
            if (isPegawaiShift != null) {
                oPegawai = pegawaiDao.findAllByJenisPegawaiKategoriPegawaiIdAndId(idKategoriPegawai, idPegawai);
                if (oPegawai != null) {
                    Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findFirstByPegawai(pegawaiCari);
                    if (pegawaiShift.isPresent()) {
                        oPegawai = pegawaiShift.get().getPegawai();
                    }
                }
            } else
                oPegawai = pegawaiDao.findAllByJenisPegawaiKategoriPegawaiIdAndId(idKategoriPegawai, idPegawai);

            if (oPegawai != null) {
                if (pegawaiUtils.isPegawaiShift(oPegawai))
                    listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawaiShift(oPegawai, tglMulai, tglSelesai);
                else
                    listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawai(oPegawai, tglMulai, tglSelesai);
            } else
                jenisReport = null;

            mm.addAttribute("data", listRekapPresensiPegawaiDto);
        }

        if (pegawaiCari == null && idKategoriPegawai != null) {

            jenisReport = "Type2";

            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawaiByPegawaiShift(listIdUnitKerja, idKategoriPegawai);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawai(listIdUnitKerja, idKategoriPegawai);

            if (resultEmployeeAdminUnit.size() == 0)
                jenisReport = null;

            if (resultEmployeeAdminUnit.size() != 0) {
                for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
                    Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
                    RekapPresensiPegawaiAdminUnitDto data;
                    if (pegawaiUtils.isPegawaiShift(pegawaiDto.get()))
                        data = absensiUtils.getRekapPresensiPerPegawaiShiftAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    else
                        data = absensiUtils.getRekapPresensiPerPegawaiAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    listRekapPresensiPegawaiAdminUnitDto.add(data);
                }
            }
            mm.addAttribute("data", listRekapPresensiPegawaiAdminUnitDto);
        }

        if (mulai != null && selesai != null && pegawaiCari == null && idKategoriPegawai == null) {

            jenisReport = "Type2";
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitByPegawaiShift(listIdUnitKerja);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnit(listIdUnitKerja);

            if (resultEmployeeAdminUnit.size() == 0)
                jenisReport = null;

            if (resultEmployeeAdminUnit.size() != 0) {
                for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
                    Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
                    RekapPresensiPegawaiAdminUnitDto data;
                    if (pegawaiUtils.isPegawaiShift(pegawaiDto.get()))
                        data = absensiUtils.getRekapPresensiPerPegawaiShiftAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    else
                        data = absensiUtils.getRekapPresensiPerPegawaiAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    listRekapPresensiPegawaiAdminUnitDto.add(data);
                }
            }
            mm.addAttribute("data", listRekapPresensiPegawaiAdminUnitDto);
        }

        mm.addAttribute("jenisReport", jenisReport);
        mm.addAttribute("idKategoriPegawai", idKategoriPegawai);
        mm.addAttribute("pegawaiCari", pegawaiCari);
        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("listIdUnitKerja", listIdUnitKerja);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);
        mm.addAttribute("listPegawai", listPegawai);
        mm.addAttribute("isPegawaiShift", isPegawaiShift);

        return "unit/laporan/rekap-pegawai";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/rekap-pegawai-print")
    public String printRekapPegawai(ModelMap mm, Authentication auth,
                                    @RequestParam(value = "mulai", required = false) String mulai,
                                    @RequestParam(value = "selesai", required = false) String selesai,
                                    @RequestParam(value = "idPegawaiCari", required = false) Long idPegawaiCari) {

        Pegawai pegawaiCari = pegawaiDao.getOne(idPegawaiCari);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawaiCari);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> listRekapPresensiPegawaiDto;
        if (pegawaiUtils.isPegawaiShift(pegawaiCari))
            listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawaiShift(pegawaiCari, tglMulai, tglSelesai);
        else
            listRekapPresensiPegawaiDto = absensiUtils.getRekapPresensiPegawai(pegawaiCari, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pegawai", pegawaiCari);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawaiCari));
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("data", listRekapPresensiPegawaiDto);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);

        return "unit/laporan/rekap-pegawai-print";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/rekap-pegawai-print-all")
    public String printRekapPegawaiAll(ModelMap mm, Authentication auth,
                                       @RequestParam(value = "mulai", required = false) String mulai,
                                       @RequestParam(value = "selesai", required = false) String selesai,
                                       @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai,
                                       @RequestParam(value = "isPegawaiShift", required = false) Boolean isPegawaiShift) {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtdByPegawaiAU(pegawai);
        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;

        if (idKategoriPegawai != null) {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawaiByPegawaiShift(listIdUnitKerja, idKategoriPegawai);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawai(listIdUnitKerja, idKategoriPegawai);
        } else {
            if (isPegawaiShift != null)
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnitByPegawaiShift(listIdUnitKerja);
            else
                resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeAdminUnitByListUnit(listIdUnitKerja);
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

        //Pejabat
        //Pegawai pejabat = pegawaiDao.findAllByUnitKerjaIdAndJabatanPegawaiIdOrderById(333L, 52L);
        Pegawai pejabat = adminUnit.getPjPegawai();
        if (pejabat != null) {
            pejabat.setNama(Pegawai.toStringNama(pejabat));
            pejabat.getJabatanPegawai().setNama(adminUnit.getPjJabatan() == null ? "" : adminUnit.getPjJabatan());
        } else
            pejabat = new Pegawai();

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", listRekapPresensiPegawaiAdminUnitDto);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("idKategoriPegawai", idKategoriPegawai);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);

        return "unit/laporan/rekap-pegawai-print-all";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @GetMapping("/admin-unit/laporan/rekap-dosen")
    public String getRekapDosen(ModelMap mm, Authentication auth,
                                @RequestParam(value = "mulai", required = false) String mulai,
                                @RequestParam(value = "selesai", required = false) String selesai,
                                @RequestParam(value = "pegawaiCari", required = false) Pegawai pegawaiCari,
                                @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai) {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);
        List<EmployeeDto> listDosen = pegawaiDao.findAllEmployeeDosenByListUnitKerja(listIdUnitKerja);

        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;
        String jenisReport = null;
        List<RekapPresensiPegawaiDto> listRekapPresensiDosenDto = null;
        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiDosenAdminUnitDto = new LinkedList<>();

        if (pegawaiCari != null && idKategoriPegawai == null) {
            listRekapPresensiDosenDto = absensiUtils.getRekapPresensiDosen(pegawaiCari, tglMulai, tglSelesai);
            jenisReport = "Type1";

            mm.addAttribute("data", listRekapPresensiDosenDto);
        }
        if (pegawaiCari != null && idKategoriPegawai != null) {
            Long idPegawai = pegawaiCari.getId();

            jenisReport = "Type1";
            pegawaiCari = pegawaiDao.findAllByJenisPegawaiKategoriPegawaiIdAndId(idKategoriPegawai, idPegawai);
            if (pegawaiCari == null)
                jenisReport = null;

            listRekapPresensiDosenDto = absensiUtils.getRekapPresensiDosen(pegawaiCari, tglMulai, tglSelesai);

            mm.addAttribute("data", listRekapPresensiDosenDto);
        }
        if (pegawaiCari == null && idKategoriPegawai != null) {

            jenisReport = "Type2";

            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByListUnitKerjaAndKategoriPegawai(listIdUnitKerja, idKategoriPegawai);

            if (resultEmployeeAdminUnit.size() == 0)
                jenisReport = null;

            if (resultEmployeeAdminUnit.size() != 0) {
                for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
                    Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
                    RekapPresensiPegawaiAdminUnitDto data = absensiUtils.getRekapPresensiPerDosenAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    listRekapPresensiDosenAdminUnitDto.add(data);
                }
            }
            mm.addAttribute("data", listRekapPresensiDosenAdminUnitDto);
        }
        if (mulai != null && selesai != null && pegawaiCari == null && idKategoriPegawai == null) {

            jenisReport = "Type2";

            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByListUnit(listIdUnitKerja);

            if (resultEmployeeAdminUnit.size() == 0)
                jenisReport = null;

            if (resultEmployeeAdminUnit.size() != 0) {
                for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
                    Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
                    RekapPresensiPegawaiAdminUnitDto data = absensiUtils.getRekapPresensiPerDosenAU(pegawaiDto.get(), tglMulai, tglSelesai);
                    listRekapPresensiDosenAdminUnitDto.add(data);
                }
            }
            mm.addAttribute("data", listRekapPresensiDosenAdminUnitDto);
        }


        mm.addAttribute("jenisReport", jenisReport);
        mm.addAttribute("idKategoriPegawai", idKategoriPegawai);
        mm.addAttribute("pegawaiCari", pegawaiCari);
        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("listIdUnitKerja", listIdUnitKerja);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);
        mm.addAttribute("listDosen", listDosen);

        return "unit/laporan/rekap-dosen";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/rekap-dosen-print")
    public String printRekapDosen(ModelMap mm, Authentication auth,
                                  @RequestParam(value = "mulai", required = false) String mulai,
                                  @RequestParam(value = "selesai", required = false) String selesai,
                                  @RequestParam(value = "idPegawaiCari", required = false) Long idPegawaiCari) {

        Pegawai pegawaiCari = pegawaiDao.getOne(idPegawaiCari);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawaiCari);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> listRekapPresensiDosenDto = absensiUtils.getRekapPresensiDosen(pegawaiCari, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pegawai", pegawaiCari);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawaiCari));
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("data", listRekapPresensiDosenDto);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);

        return "unit/laporan/rekap-dosen-print";
    }

    @PreAuthorize("hasAuthority('aunit')")
    @PostMapping("/admin-unit/laporan/rekap-dosen-print-all")
    public String printRekapDosenAll(ModelMap mm, Authentication auth,
                                     @RequestParam(value = "mulai", required = false) String mulai,
                                     @RequestParam(value = "selesai", required = false) String selesai,
                                     @RequestParam(value = "idKategoriPegawai", required = false) Long idKategoriPegawai) {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtdByPegawaiAU(pegawai);
        List<Long> listIdUnitKerja = absensiUtils.generateListUnitKerjaAdminUnit(pegawai);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);
        List<EmployeeAdminUnitDto> resultEmployeeAdminUnit = null;

        if (idKategoriPegawai != null)
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByListUnitKerjaAndKategoriPegawai(listIdUnitKerja, idKategoriPegawai);
        else
            resultEmployeeAdminUnit = pegawaiDao.findAllEmployeeDosenAdminUnitByListUnit(listIdUnitKerja);

        List<RekapPresensiPegawaiAdminUnitDto> listRekapPresensiDosenAdminUnitDto = new LinkedList<>();

        for (EmployeeAdminUnitDto employeeAdminUnitDto : resultEmployeeAdminUnit) {
            Optional<Pegawai> pegawaiDto = pegawaiDao.findById(employeeAdminUnitDto.getId());
            RekapPresensiPegawaiAdminUnitDto data = absensiUtils.getRekapPresensiPerDosenAU(pegawaiDto.get(), tglMulai, tglSelesai);
            listRekapPresensiDosenAdminUnitDto.add(data);
        }

        //Pejabat
        //Pegawai pejabat = pegawaiDao.findAllByUnitKerjaIdAndJabatanPegawaiIdOrderById(333L, 52L);
        Pegawai pejabat = adminUnit.getPjPegawai();
        if (pejabat != null) {
            pejabat.setNama(Pegawai.toStringNama(pejabat));
            pejabat.getJabatanPegawai().setNama(adminUnit.getPjJabatan() == null ? "" : adminUnit.getPjJabatan());
        } else
            pejabat = new Pegawai();

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", listRekapPresensiDosenAdminUnitDto);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("idKategoriPegawai", idKategoriPegawai);
        mm.addAttribute("listKategoriPegawai", kategoriPegawaiDao.findAll());
        mm.addAttribute("currentService", currentService);

        return "unit/laporan/rekap-dosen-print-all";
    }
}
