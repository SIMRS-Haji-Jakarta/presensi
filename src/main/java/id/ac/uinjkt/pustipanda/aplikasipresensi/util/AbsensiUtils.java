package id.ac.uinjkt.pustipanda.aplikasipresensi.util;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AbsensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.HariKerjaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AbsensiUtils {
    @Autowired
    private AbsensiDao absensiDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private StatusPresensiDao statusPresensiDao;

    public static Boolean isBetween(double x, double lower, double upper) {
        return lower <= x && x <= upper;
    }

    public Map<String, Integer> getListBulan(LocalDate tglMulai, LocalDate tglSelesai) {
        Map<String, Integer> listBulan = new HashMap<>();// ArrayMap<>();
        List<HariKerja> hariKerjas = hariKerjaDao.findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(tglMulai, tglSelesai);

        long i;
        int intBulan = 0;
        for (HariKerja hariKerja : hariKerjas) {
            if (hariKerja.getTanggal().getMonthValue() != intBulan) {
                i = hariKerjaDao.hitungJumlahHariPerBulan(hariKerja.getTanggal().getMonthValue(), tglMulai, tglSelesai);
                listBulan.put(hariKerja.getTanggal().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id-ID")), (int) i);
            }
            intBulan = hariKerja.getTanggal().getMonthValue();
        }

        return listBulan;
    }

    public RekapPresensiPegawaiAdminUnitDto getRekapPresensiPerPegawaiAU(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        RekapPresensiPegawaiAdminUnitDto data = new RekapPresensiPegawaiAdminUnitDto();

        int totalHari = 0;
        int totalHadir = 0;
        int totalHariKerja = 0;
        int totalCuti = 0;
        int totalIzin = 0;
        int totalSakit = 0;
        int totalDinasLuar = 0;
        int totalTidakAbsen = 0;
        int totalAlpa = 0;

        //int totalJamTerlambat = 0;
        //int totalMenitTerlambat = 0;
        long totalDetikTerlambat = 0L;

        //int totalJamKecepetan = 0;
        //int totalMenitKecepetan = 0;
        long totalDetikKecepetan = 0L;

        //int totalJamKekurangan = 0;
        //int totalMenitKekurangan = 0;
        long totalDetikKekurangan = 0L;

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {

            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariKerjaRangeTanggal(waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int izin = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_IZIN, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();
            Duration duTerlambat = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.TERLAMBAT);
            Duration duKecepatan = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.KECEPATAN);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            totalHariKerja = totalHariKerja + hariKerja;
            totalHadir = totalHadir + hadir;
            totalCuti = totalCuti + cuti;
            totalIzin = totalIzin + izin;
            totalSakit = totalSakit + sakit;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalAlpa = totalAlpa + alpa;

            //totalJamTerlambat = totalJamTerlambat + (int) duTerlambat.toHours();
            //totalMenitTerlambat = totalMenitTerlambat + (int) duTerlambat.toMinutes();
            totalDetikTerlambat = totalDetikTerlambat + (int) duTerlambat.getSeconds();

            //totalJamKecepetan = totalJamKecepetan + (int) duKecepatan.toHours();
            //totalMenitKecepetan = totalMenitKecepetan + (int) duKekurangan.toMinutes();
            totalDetikKecepetan = totalDetikKecepetan + duKecepatan.getSeconds();

            //totalJamKekurangan = totalJamKekurangan + (int) duKekurangan.toHours();
            //totalMenitKekurangan = totalMenitKekurangan + (int) duKekurangan.toMinutes();
            totalDetikKekurangan = totalDetikKekurangan + duKekurangan.getSeconds();

            totalHari = totalHari + hari;

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currentDate = tglMulai;

            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariKerjaRangeTanggal(currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int izin = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_IZIN, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.TERLAMBAT);
            Duration duKecepatan = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.KECEPATAN);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            totalHariKerja = totalHariKerja + hariKerja;
            totalHadir = totalHadir + hadir;
            totalCuti = totalCuti + cuti;
            totalIzin = totalIzin + izin;
            totalSakit = totalSakit + sakit;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalAlpa = totalAlpa + alpa;

            //totalJamTerlambat = totalJamTerlambat + (int) duTerlambat.toHours();
            //totalMenitTerlambat = totalMenitTerlambat + (int) duTerlambat.toMinutes();
            totalDetikTerlambat = totalDetikTerlambat + duTerlambat.getSeconds();

            //totalJamKecepetan = totalJamKecepetan + (int) duKecepatan.toHours();
            //totalMenitKecepetan = totalMenitKecepetan + (int) duKekurangan.toMinutes();
            totalDetikKecepetan = totalDetikKecepetan + duKecepatan.getSeconds();

            //totalJamKekurangan = totalJamKekurangan + (int) duKekurangan.toHours();
            //totalMenitKekurangan = totalMenitKekurangan + (int) duKekurangan.toMinutes();
            totalDetikKekurangan = totalDetikKekurangan + duKekurangan.getSeconds();

            totalHari = totalHari + hari;
        }

        //Duration durationTerlambat = Duration.ofHours(totalJamTerlambat).plusMinutes(totalMenitTerlambat).plusSeconds(totalDetikTerlambat);
        //Duration durationKecepetan = Duration.ofHours(totalJamKecepetan).plusMinutes(totalMenitKecepetan).plusSeconds(totalDetikKecepetan);
        //Duration durationKekurangan = Duration.ofHours(totalJamKekurangan).plusMinutes(totalMenitKekurangan).plusSeconds(totalDetikKekurangan);

        Long totalWaktuTerlambat = totalDetikTerlambat;
        Long totalWaktuKecepetan = totalDetikKecepetan;
        Long totalWaktuKekurangan = totalDetikKekurangan;

        //set data
        data.setIdPegawai(pegawai.getId());
        data.setNoAbsen(pegawai.getNomorAbsen());
        data.setNama(Pegawai.toStringNama(pegawai));
        data.setNip(pegawai.getNip());
        data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
        data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
        data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : (pegawai.getUnitKerjaPresensi().getUnit() == null
                ? pegawai.getUnitKerjaPresensi().getNama() : pegawai.getUnitKerjaPresensi().getUnit().getNama()));
        data.setHariKerja(totalHariKerja);
        data.setHadir(totalHadir);
        data.setCuti(totalCuti);
        data.setIzin(totalIzin);
        data.setSakit(totalSakit);
        data.setDinasLuar(totalDinasLuar);
        data.setTidakAbsen(totalTidakAbsen);
        data.setAlpa(totalAlpa);
        data.setTerlambat(totalWaktuTerlambat);
        data.setKecepetan(totalWaktuKecepetan);
        data.setJamKurang(totalWaktuKekurangan);
        data.setTimeTerlambat(totalWaktuTerlambat);
        data.setTimeKecepetan(totalWaktuKecepetan);
        data.setTimeJamKurang(totalWaktuKekurangan);
        data.setHariKurang(totalHari);
        data.setAktualKerja((totalHadir + totalDinasLuar) - totalHari);

        return data;
    }

    public RekapPresensiPegawaiAdminUnitDto getRekapPresensiPerPegawaiShiftAU(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        RekapPresensiPegawaiAdminUnitDto data = new RekapPresensiPegawaiAdminUnitDto();

        int totalHari = 0;
        int totalHadir = 0;
        int totalHariKerja = 0;
        int totalCuti = 0;
        int totalIzin = 0;
        int totalSakit = 0;
        int totalDinasLuar = 0;
        int totalTidakAbsen = 0;
        int totalAlpa = 0;

        //int totalJamTerlambat = 0;
        //int totalMenitTerlambat = 0;
        long totalDetikTerlambat = 0L;

        //int totalJamKecepetan = 0;
        //int totalMenitKecepetan = 0;
        long totalDetikKecepetan = 0L;

        //int totalJamKekurangan = 0;
        //int totalMenitKekurangan = 0;
        long totalDetikKekurangan = 0L;

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {

            //int hariLiburPegawaiShift = Optional.ofNullable(hariKerjaDao.hitungHariLiburPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariJadwalPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);// - hariLiburPegawaiShift;
            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariKerjaPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.TERLAMBAT_SHIFT);
            Duration duKecepatan = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.KECEPATAN_SHIFT);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            totalHariKerja = totalHariKerja + hariKerja;
            totalHadir = totalHadir + hadir;
            totalCuti = totalCuti + cuti;
            //totalIzin = totalIzin + izin;
            totalSakit = totalSakit + sakit;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalAlpa = totalAlpa + alpa;

            //totalJamTerlambat = totalJamTerlambat + (int) duTerlambat.toHours();
            //totalMenitTerlambat = totalMenitTerlambat + (int) duTerlambat.toMinutes();
            totalDetikTerlambat = totalDetikTerlambat + duTerlambat.getSeconds();

            //totalJamKecepetan = totalJamKecepetan + (int) duKecepatan.toHours();
            //totalMenitKecepetan = totalMenitKecepetan + (int) duKekurangan.toMinutes();
            totalDetikKecepetan = totalDetikKecepetan + duKecepatan.getSeconds();

            //totalJamKekurangan = totalJamKekurangan + (int) duKekurangan.toHours();
            //totalMenitKekurangan = totalMenitKekurangan + (int) duKekurangan.toMinutes();
            totalDetikKekurangan = totalDetikKekurangan + duKekurangan.getSeconds();

            totalHari = totalHari + hari;

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currentDate = tglMulai;

            //int hariLiburPegawaiShift = Optional.ofNullable(hariKerjaDao.hitungHariLiburPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariJadwalPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);  // - hariLiburPegawaiShift;
            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            //    int izin = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_IZIN, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariKerjaPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.TERLAMBAT_SHIFT);
            Duration duKecepatan = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.KECEPATAN_SHIFT);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            totalHariKerja = totalHariKerja + hariKerja;
            totalHadir = totalHadir + hadir;
            totalCuti = totalCuti + cuti;
            //      totalIzin = totalIzin + izin;
            totalSakit = totalSakit + sakit;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalAlpa = totalAlpa + alpa;

            //totalJamTerlambat = totalJamTerlambat + (int) duTerlambat.toHours();
            //totalMenitTerlambat = totalMenitTerlambat + (int) duTerlambat.toMinutes();
            totalDetikTerlambat = totalDetikTerlambat + duTerlambat.getSeconds();

            //totalJamKecepetan = totalJamKecepetan + (int) duKecepatan.toHours();
            //totalMenitKecepetan = totalMenitKecepetan + (int) duKekurangan.toMinutes();
            totalDetikKecepetan = totalDetikKecepetan + duKecepatan.getSeconds();

            //totalJamKekurangan = totalJamKekurangan + (int) duKekurangan.toHours();
            //totalMenitKekurangan = totalMenitKekurangan + (int) duKekurangan.toMinutes();
            totalDetikKekurangan = totalDetikKekurangan + duKekurangan.getSeconds();

            totalHari = totalHari + hari;
        }

        //Duration durationTerlambat = Duration.ofHours(totalJamTerlambat).plusMinutes(totalMenitTerlambat).plusSeconds(totalDetikTerlambat);
        //Duration durationKecepetan = Duration.ofHours(totalJamKecepetan).plusMinutes(totalMenitKecepetan).plusSeconds(totalDetikKecepetan);
        //Duration durationKekurangan = Duration.ofHours(totalJamKekurangan).plusMinutes(totalMenitKekurangan).plusSeconds(totalDetikKekurangan);

        Long totalWaktuTerlambat = totalDetikTerlambat;
        Long totalWaktuKecepetan = totalDetikKecepetan;
        Long totalWaktuKekurangan = totalDetikKekurangan;

        //set data
        data.setIdPegawai(pegawai.getId());
        data.setNoAbsen(pegawai.getNomorAbsen());
        data.setNama(Pegawai.toStringNama(pegawai));
        data.setNip(pegawai.getNip());
        data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
        data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
        data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : (pegawai.getUnitKerjaPresensi().getUnit() == null
                ? pegawai.getUnitKerjaPresensi().getNama() : pegawai.getUnitKerjaPresensi().getUnit().getNama()));
        data.setHariKerja(totalHariKerja);
        data.setHadir(totalHadir);
        data.setCuti(totalCuti);
        // data.setIzin(totalIzin);
        data.setSakit(totalSakit);
        data.setDinasLuar(totalDinasLuar);
        data.setTidakAbsen(totalTidakAbsen);
        data.setAlpa(totalAlpa);
        data.setTerlambat(totalWaktuTerlambat);
        data.setKecepetan(totalWaktuKecepetan);
        data.setJamKurang(totalWaktuKekurangan);
        data.setTimeTerlambat(totalWaktuTerlambat);
        data.setTimeKecepetan(totalWaktuKecepetan);
        data.setTimeJamKurang(totalWaktuKekurangan);
        data.setHariKurang(totalHari);
        data.setAktualKerja((totalHadir + totalDinasLuar) - totalHari);

        return data;
    }

    public RekapPresensiPegawaiAdminUnitDto getRekapPresensiPerDosenAU(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        RekapPresensiPegawaiAdminUnitDto data = new RekapPresensiPegawaiAdminUnitDto();

        int totalHadir = 0;
        int totalDinasLuar = 0;
        int totalTidakAbsen = 0;
        long totalJamKerja = 0L;

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {

            data.setTahun(waktu.getYear());
            data.setBulan(waktu.getMonthValue());
            data.setBulanNama(AppConstant.BULAN[waktu.getMonthValue() - 1]);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            data.setHadir(hadir);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            data.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            data.setTidakAbsen(tidakAbsen);

            Duration durationJamKerja = Duration.ofHours(0).plusMinutes(0).plusSeconds(0);
            LocalDate tglSelesaiPerBulan = tglSelesai;
            if (waktu.getMonthValue() != tglSelesai.getMonthValue()) {
                YearMonth month = YearMonth.from(waktu);
                tglSelesaiPerBulan = month.atEndOfMonth();
            }
            for (LocalDate tanggalIni = waktu; tanggalIni.isBefore(tglSelesaiPerBulan.plusDays(1)); tanggalIni = tanggalIni.plusDays(1)) {
                LocalTime jamKerja = Optional.ofNullable(absensiDao.hitungJumlahJamKerjaPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                durationJamKerja = durationJamKerja.plus(Duration.ofHours(jamKerja.getHour()).plusMinutes(jamKerja.getMinute()).plusSeconds(jamKerja.getSecond()));
            }
            Duration durationDinasLuar = Duration.ofHours(dinasLuar * 3L);
            data.setJamKerja(durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());

            totalHadir = totalHadir + hadir;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalJamKerja = totalJamKerja + (durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currrentDate = tglMulai;

            data.setTahun(currrentDate.getYear());
            data.setBulan(currrentDate.getMonthValue());
            data.setBulanNama(AppConstant.BULAN[currrentDate.getMonthValue() - 1]);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, currrentDate.getYear(), currrentDate.getMonthValue(), currrentDate, currrentDate)).orElse(0);
            data.setHadir(hadir);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currrentDate.getYear(), currrentDate.getMonthValue(), currrentDate, currrentDate)).orElse(0);
            data.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, currrentDate.getYear(), currrentDate.getMonthValue(), currrentDate, currrentDate)).orElse(0);
            data.setTidakAbsen(tidakAbsen);

            Duration durationJamKerja = Duration.ofHours(0).plusMinutes(0).plusSeconds(0);
            LocalTime jamKerja = Optional.ofNullable(absensiDao.hitungJumlahJamKerjaPerTanggal(pegawai, currrentDate)).orElse(LocalTime.of(0, 0, 0));
            durationJamKerja = durationJamKerja.plus(Duration.ofHours(jamKerja.getHour()).plusMinutes(jamKerja.getMinute()).plusSeconds(jamKerja.getSecond()));
            Duration durationDinasLuar = Duration.ofHours(dinasLuar * 3L);
            data.setJamKerja(durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());

            totalHadir = totalHadir + hadir;
            totalDinasLuar = totalDinasLuar + dinasLuar;
            totalTidakAbsen = totalTidakAbsen + tidakAbsen;
            totalJamKerja = totalJamKerja + (durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());
        }

        data.setIdPegawai(pegawai.getId());
        data.setNoAbsen(pegawai.getNomorAbsen());
        data.setNama(Pegawai.toStringNama(pegawai));
        data.setNip(pegawai.getNip());
        data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
        data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
        data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : (pegawai.getUnitKerjaPresensi().getUnit() == null
                ? pegawai.getUnitKerjaPresensi().getNama() : pegawai.getUnitKerjaPresensi().getUnit().getNama()));
        data.setHadir(totalHadir);
        data.setDinasLuar(totalDinasLuar);
        data.setTidakAbsen(totalTidakAbsen);
        data.setJamKerja(totalJamKerja);

        return data;
    }

    public RekapHarianPegawaiDto getRekapHarianAll(Pegawai pegawai, LocalDate tanggal) {

        RekapHarianPegawaiDto data = new RekapHarianPegawaiDto();

        data.setIdPegawai(pegawai.getId());
        data.setNama(Pegawai.toStringNama(pegawai));
        data.setNip(pegawai.getNip());
        data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());

        Optional<Absensi> absensi = absensiDao.findAllByPegawaiAndTanggalOrderById(pegawai, tanggal);

        data.setWfh(absensi.map(value -> value.getWaktuIn() == null ? "" : value.getWaktuIn().toString()).orElse(""));
        data.setWfo("");
        data.setSakit(Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPerTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, tanggal)).orElse(0));
        data.setCuti(Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPerTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, tanggal)).orElse(0));
        data.setDinasLuar(Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPerTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, tanggal)).orElse(0));
        data.setTugasBelajar(Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPerTanggalByStatus(pegawai, AppConstant.STATUS_TUGAS_BELAJAR, tanggal)).orElse(0));
        data.setTanpaKeterangan(Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPerTanggal(pegawai, tanggal)).orElse(0));

        return data;
    }

    public List<Long> generateListUnitKerjaAdminUnit(Pegawai pegawai) {

        AdminUnit adminUnit = adminUnitDao.findByPegawai(pegawai);

        List<UnitKerjaPresensi> unitKerjas = new ArrayList<>(adminUnit.getUnitKerja());

        List<Long> result = new ArrayList<>();
        for (UnitKerjaPresensi unitKerja : unitKerjas) {
            result.add(unitKerja.getId());
        }
        return result;
    }

    public List<RekapPresensiPegawaiDto> getRekapPresensiPegawai(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        List<RekapPresensiPegawaiDto> datas = new ArrayList<>();
        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            dto.setTahun(waktu.getYear());
            dto.setBulan(waktu.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[waktu.getMonthValue() - 1]);

            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariKerjaRangeTanggal(waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setHariKerja(hariKerja);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setHadir(hadir);

            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setCuti(cuti);

            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setSakit(sakit);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setAlpa(alpa);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.TERLAMBAT);
            Duration duKecepatan = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.KECEPATAN);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
            //dto.setTerlambat(terlambat);
            //dto.setKecepetan(kecepetan);
            //dto.setJamKurang(kekurangan);
            //dto.setTimeTerlambat(terlambat.toSecondOfDay());
            dto.setTimeTerlambat(duTerlambat.getSeconds());
            //dto.setTimeKecepetan(kecepetan.toSecondOfDay());
            dto.setTimeKecepetan(duKecepatan.getSeconds());
            //dto.setTimeJamKurang(kekurangan.toSecondOfDay());
            dto.setTimeJamKurang(duKekurangan.getSeconds());
            dto.setHariKurang(hari);
            dto.setAktualKerja((hadir + dinasLuar) - hari);

            datas.add(dto);
            //LOGGER.info("RekapPresensiPegawaiDto: {}", dto);
        }

        //jika value tanggal mulai dan selesai sama
        if (datas.size() == 0 && tglMulai.equals(tglSelesai)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            LocalDate currentdate = tglMulai;

            dto.setTahun(currentdate.getYear());
            dto.setBulan(currentdate.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[currentdate.getMonthValue() - 1]);

            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariKerjaRangeTanggal(currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setHariKerja(hariKerja);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setHadir(hadir);

            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setCuti(cuti);

            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setSakit(sakit);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganRangeTanggal(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setAlpa(alpa);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatRangeTanggal(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanRangeTanggal(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();
            Duration duTerlambat = getExactDuration(currentdate, tglSelesai, pegawai, AppConstant.TERLAMBAT);
            Duration duKecepatan = getExactDuration(currentdate, tglSelesai, pegawai, AppConstant.KECEPATAN);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
            //dto.setTerlambat(terlambat);
            //dto.setKecepetan(kecepetan);
            //dto.setJamKurang(kekurangan);
            dto.setTimeTerlambat(duTerlambat.getSeconds());
            dto.setTimeKecepetan(duKecepatan.getSeconds());
            dto.setTimeJamKurang(duKekurangan.getSeconds());
            dto.setHariKurang(hari);
            dto.setAktualKerja((hadir + dinasLuar) - hari);

            datas.add(dto);
        }

        return datas;
    }

    public List<RekapPresensiPegawaiDto> getRekapPresensiPegawaiShift(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        List<RekapPresensiPegawaiDto> datas = new ArrayList<>();
        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            dto.setTahun(waktu.getYear());
            dto.setBulan(waktu.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[waktu.getMonthValue() - 1]);

            //int hariLiburPegawaiShift = Optional.ofNullable(hariKerjaDao.hitungHariLiburPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariJadwalPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);// - hariLiburPegawaiShift;
            dto.setHariKerja(hariKerja);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setHadir(hadir);

            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setCuti(cuti);

            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setSakit(sakit);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariKerjaPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setAlpa(alpa);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.TERLAMBAT_SHIFT);
            Duration duKecepatan = getExactDuration(waktu, tglSelesai, pegawai, AppConstant.KECEPATAN_SHIFT);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
            //dto.setTerlambat(terlambat);
            //dto.setKecepetan(kecepetan);
            //dto.setJamKurang(kekurangan);
            dto.setTimeTerlambat(duTerlambat.getSeconds());
            dto.setTimeKecepetan(duKecepatan.getSeconds());
            dto.setTimeJamKurang(duKekurangan.getSeconds());
            dto.setHariKurang(hari);
            dto.setAktualKerja((hadir + dinasLuar) - hari);

            datas.add(dto);
            //LOGGER.info("RekapPresensiPegawaiDto: {}", dto);
        }

        //jika value tanggal mulai dan selesai sama
        if (datas.size() == 0 && tglMulai.equals(tglSelesai)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            LocalDate currentDate = tglMulai;

            dto.setTahun(currentDate.getYear());
            dto.setBulan(currentDate.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[currentDate.getMonthValue() - 1]);

            //int hariLiburPegawaiShift = Optional.ofNullable(hariKerjaDao.hitungHariLiburPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            int hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariJadwalPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);// - hariLiburPegawaiShift;
            dto.setHariKerja(hariKerja);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setHadir(hadir);

            int cuti = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_CUTI, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setCuti(cuti);

            int sakit = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_SAKIT, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setSakit(sakit);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariKerjaPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            int alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
            dto.setAlpa(alpa);

            //LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0)); // : hariKerjaDao.hitungTerlambat(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanPegawaiShiftRangeTanggal(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(LocalTime.of(0, 0, 0));// : hariKerjaDao.hitungKecepetan(pegawai, waktu.getYear(), waktu.getMonthValue());
            //LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
            //Integer kurangJam = kekurangan.getHour();

            Duration duTerlambat = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.TERLAMBAT_SHIFT);
            Duration duKecepatan = getExactDuration(currentDate, tglSelesai, pegawai, AppConstant.KECEPATAN_SHIFT);
            Duration duKekurangan = duTerlambat.plus(duKecepatan);
            Long longHours = duKekurangan.toHours();
            Long longMinutes = (duKekurangan.getSeconds() % 3600) / 60;

            Double kurangJam = minutesToHours(longHours.intValue(), longMinutes.intValue());
            //Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
            Integer hari = getJumlahHari(kurangJam);

            //LOGGER.info("terlambat: {}, kecepetan: {}, kekurangan: {}, jam: {}", terlambat, kecepetan, kekurangan, kurangJam);
            //dto.setTerlambat(terlambat);
            //dto.setKecepetan(kecepetan);
            //dto.setJamKurang(kekurangan);
            dto.setTimeTerlambat(duTerlambat.getSeconds());
            dto.setTimeKecepetan(duKecepatan.getSeconds());
            dto.setTimeJamKurang(duKekurangan.getSeconds());
            dto.setHariKurang(hari);
            dto.setAktualKerja((hadir + dinasLuar) - hari);

            datas.add(dto);
        }

        return datas;
    }

    public List<RekapPresensiPegawaiDto> getRekapPresensiDosen(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai) {
        List<RekapPresensiPegawaiDto> datas = new ArrayList<>();

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusMonths(1)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            dto.setTahun(waktu.getYear());
            dto.setBulan(waktu.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[waktu.getMonthValue() - 1]);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setHadir(hadir);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), tglMulai, tglSelesai)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            Duration durationJamKerja = Duration.ofHours(0).plusMinutes(0).plusSeconds(0);
            LocalDate tglSelesaiPerBulan = tglSelesai;
            if (waktu.getMonthValue() != tglSelesai.getMonthValue()) {
                YearMonth month = YearMonth.from(waktu);
                tglSelesaiPerBulan = month.atEndOfMonth();
            }
            for (LocalDate tanggalIni = waktu; tanggalIni.isBefore(tglSelesaiPerBulan.plusDays(1)); tanggalIni = tanggalIni.plusDays(1)) {
                LocalTime jamKerja = Optional.ofNullable(absensiDao.hitungJumlahJamKerjaPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                durationJamKerja = durationJamKerja.plus(Duration.ofHours(jamKerja.getHour()).plusMinutes(jamKerja.getMinute()).plusSeconds(jamKerja.getSecond()));
            }
            Duration durationDinasLuar = Duration.ofHours(dinasLuar * 3L);
            dto.setJamKerja(durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());

            datas.add(dto);
        }

        //jika value tanggal mulai dan selesai sama
        if (datas.size() == 0 && tglMulai.equals(tglSelesai)) {
            RekapPresensiPegawaiDto dto = new RekapPresensiPegawaiDto();
            LocalDate currentdate = tglMulai;

            dto.setTahun(currentdate.getYear());
            dto.setBulan(currentdate.getMonthValue());
            dto.setBulanNama(AppConstant.BULAN[currentdate.getMonthValue() - 1]);

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setHadir(hadir);

            int dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukRangeTanggalByStatus(pegawai, AppConstant.STATUS_DINAS_LUAR, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setDinasLuar(dinasLuar);

            int tidakAbsen = Optional.ofNullable(absensiDao.hitungJumlahTidakPresensiHariMasukRangeTanggal(pegawai, currentdate.getYear(), currentdate.getMonthValue(), currentdate, currentdate)).orElse(0);
            dto.setTidakAbsen(tidakAbsen);

            Duration durationJamKerja = Duration.ofHours(0).plusMinutes(0).plusSeconds(0);
            LocalTime jamKerja = Optional.ofNullable(absensiDao.hitungJumlahJamKerjaPerTanggal(pegawai, currentdate)).orElse(LocalTime.of(0, 0, 0));
            durationJamKerja = durationJamKerja.plus(Duration.ofHours(jamKerja.getHour()).plusMinutes(jamKerja.getMinute()).plusSeconds(jamKerja.getSecond()));
            Duration durationDinasLuar = Duration.ofHours(dinasLuar * 3L);
            dto.setJamKerja(durationJamKerja.getSeconds() + durationDinasLuar.getSeconds());

            datas.add(dto);
        }

        return datas;
    }

    public AktualDto getAktualHariKerjaByTahunAndBulanAndPegawai(Integer year, Integer month, Pegawai pegawai) {
        Integer hariKerja = Optional.ofNullable(hariKerjaDao.hitungHariKerja(year, month)).orElse(0);
        Integer hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirByYearAndMonthAndPegawai(pegawai, year, month)).orElse(0);
        Integer dinasLuar = Optional.ofNullable(absensiDao.hitungJumlahPresensiTidakMasukByPegawaiAndStatusAndYearAndMonth(pegawai, AppConstant.STATUS_DINAS_LUAR, year, month)).orElse(0);

        LocalTime terlambat = Optional.ofNullable(hariKerjaDao.hitungTerlambatByPegawaiAndYearAndMonth(pegawai, year, month)).orElse(LocalTime.of(0, 0, 0));
        LocalTime kecepetan = Optional.ofNullable(hariKerjaDao.hitungKecepetanByPegawaiAndYearAndMonth(pegawai, year, month)).orElse(LocalTime.of(0, 0, 0));
        LocalTime kekurangan = terlambat.plusHours(kecepetan.getHour()).plusMinutes(kecepetan.getMinute()).plusSeconds(kecepetan.getSecond());
        //Integer kurangJam = kekurangan.getHour();
        Double kurangJam = minutesToHours(kekurangan.getHour(), kekurangan.getMinute());
        Integer hari = getJumlahHari(kurangJam);

        Integer aktual = (hadir + dinasLuar) - hari;

        return AktualDto.builder()
                .pegawai(pegawai.getUuid())
                .jumlah(hariKerja)
                .aktual(aktual).build();
    }

    public List<RekapKetidakhadiranPegawaiAdminUnitDto> getRekapKetidakhadiranPerPegawaiAU(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai, StatusPresensi statusPresensi) {
        List<RekapKetidakhadiranPegawaiAdminUnitDto> datas = new LinkedList<>();

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusDays(1)) {

            RekapKetidakhadiranPegawaiAdminUnitDto data = new RekapKetidakhadiranPegawaiAdminUnitDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggal(waktu);
            if (hariKerja.isPresent()) {
                if (hariKerja.get().getStatusHari().getId().equals(AppConstant.HARI_LIBUR.getId()))
                    continue;
            } else
                continue;

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, waktu.getYear(), waktu.getMonthValue(), waktu, waktu)).orElse(0);
            if (hadir > 0) {
                continue;
            }
            String status = getStatusKetidakhadiranByDateAndPegawai(pegawai, waktu, statusPresensi, false);

            if (statusPresensi == null || statusPresensi.getNama().equals(status)) {
                data.setIdPegawai(pegawai.getId());
                data.setTanggal(waktu);
                data.setNama(Pegawai.toStringNama(pegawai));
                data.setNip(pegawai.getNip());
                data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
                data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
                data.setKategoriPegawai(pegawai.getJenisPegawai().getKategoriPegawai() == null ? "" : pegawai.getJenisPegawai().getKategoriPegawai().getNama());
                data.setJenisKelamin(pegawai.getJenisKelamin());
                data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
                data.setStatus(status);

                datas.add(data);
            }
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currentDate = tglMulai;

            RekapKetidakhadiranPegawaiAdminUnitDto data = new RekapKetidakhadiranPegawaiAdminUnitDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggal(currentDate);
            if (hariKerja.isPresent() && !hariKerja.get().getStatusHari().getId().equals(AppConstant.HARI_LIBUR.getId())) {
                int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirRangeTanggalHariKerjaMasuk(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
                if (hadir == 0) {
                    String status = getStatusKetidakhadiranByDateAndPegawai(pegawai, currentDate, statusPresensi, false);

                    data.setIdPegawai(pegawai.getId());
                    data.setTanggal(currentDate);
                    data.setNama(Pegawai.toStringNama(pegawai));
                    data.setNip(pegawai.getNip());
                    data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
                    data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
                    data.setKategoriPegawai(pegawai.getJenisPegawai().getKategoriPegawai() == null ? "" : pegawai.getJenisPegawai().getKategoriPegawai().getNama());
                    data.setJenisKelamin(pegawai.getJenisKelamin());
                    data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
                    data.setStatus(status);

                    datas.add(data);
                }
            }
        }

        return datas;
    }

    public List<RekapKetidakhadiranPegawaiAdminUnitDto> getRekapKetidakhadiranPerPegawaiShiftAU(Pegawai pegawai, LocalDate tglMulai, LocalDate tglSelesai, StatusPresensi statusPresensi) {
        List<RekapKetidakhadiranPegawaiAdminUnitDto> datas = new LinkedList<>();

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai); waktu = waktu.plusDays(1)) {

            RekapKetidakhadiranPegawaiAdminUnitDto data = new RekapKetidakhadiranPegawaiAdminUnitDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggalAndPegawaiShift(waktu, pegawai);
            //cek apakah hari kerja adalah hari masuk untuk pegawai shift
            if (!hariKerja.isPresent())
                continue;

            int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, waktu.getYear(), waktu.getMonthValue(), waktu, waktu)).orElse(0);
            if (hadir > 0) {
                continue;
            }
            String status = getStatusKetidakhadiranByDateAndPegawai(pegawai, waktu, statusPresensi, true);

            if (statusPresensi == null || statusPresensi.getNama().equals(status)) {
                data.setIdPegawai(pegawai.getId());
                data.setTanggal(waktu);
                data.setNama(Pegawai.toStringNama(pegawai));
                data.setNip(pegawai.getNip());
                data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
                data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
                data.setKategoriPegawai(pegawai.getJenisPegawai().getKategoriPegawai() == null ? "" : pegawai.getJenisPegawai().getKategoriPegawai().getNama());
                data.setJenisKelamin(pegawai.getJenisKelamin());
                data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
                data.setStatusPegawaiShift(true);
                data.setStatus(status);

                datas.add(data);
            }
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currentDate = tglMulai;

            RekapKetidakhadiranPegawaiAdminUnitDto data = new RekapKetidakhadiranPegawaiAdminUnitDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggalAndPegawaiShift(currentDate, pegawai);
            if (hariKerja.isPresent()) {
                int hadir = Optional.ofNullable(absensiDao.hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(pegawai, currentDate.getYear(), currentDate.getMonthValue(), currentDate, currentDate)).orElse(0);
                if (hadir == 0) {
                    String status = getStatusKetidakhadiranByDateAndPegawai(pegawai, currentDate, statusPresensi, true);

                    data.setIdPegawai(pegawai.getId());
                    data.setTanggal(currentDate);
                    data.setNama(Pegawai.toStringNama(pegawai));
                    data.setNip(pegawai.getNip());
                    data.setGolongan(pegawai.getGolongan() == null ? "" : pegawai.getGolongan().getNama());
                    data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
                    data.setKategoriPegawai(pegawai.getJenisPegawai().getKategoriPegawai() == null ? "" : pegawai.getJenisPegawai().getKategoriPegawai().getNama());
                    data.setJenisKelamin(pegawai.getJenisKelamin());
                    data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
                    data.setStatusPegawaiShift(true);
                    data.setStatus(status);

                    datas.add(data);
                }
            }
        }

        return datas;
    }

    public List<RekapAbsensiCsvPegawaiDto> getRekapAbsensiCsvPerPegawai(Pegawai pegawai, Integer tahun, Integer bulan) {
        List<RekapAbsensiCsvPegawaiDto> datas = new LinkedList<>();

        YearMonth date = YearMonth.of(tahun, bulan);
        LocalDate tglMulai = date.atDay(1);
        LocalDate tglSelesai = date.atEndOfMonth();

        for (LocalDate waktu = tglMulai; waktu.isBefore(tglSelesai.plusDays(1)); waktu = waktu.plusDays(1)) {

            RekapAbsensiCsvPegawaiDto data = new RekapAbsensiCsvPegawaiDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggal(waktu);
            if (hariKerja.isPresent()) {
                if (hariKerja.get().getStatusHari().getId().equals(AppConstant.HARI_LIBUR.getId()))
                    continue;
            } else
                continue;

            String status = getStatusKehadiran(pegawai, waktu, false);

            data.setIdPegawai(pegawai.getId());
            data.setNomorAbsen(pegawai.getNomorAbsen() == null ? "" : pegawai.getNomorAbsen());
            data.setNama(Pegawai.toStringNama(pegawai));
            data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
            data.setTanggal(waktu);
            data.setWaktuIn(getWaktuInByDateAndPegawai(pegawai, waktu));
            data.setWaktuOut(getWaktuOutByDateAndPegawai(pegawai, waktu));
            data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
            data.setKeterangan(status);

            datas.add(data);
        }

        //jika value tanggal mulai dan selesai sama
        if (tglMulai.equals(tglSelesai)) {
            LocalDate currentDate = tglMulai;

            RekapAbsensiCsvPegawaiDto data = new RekapAbsensiCsvPegawaiDto();
            Optional<HariKerja> hariKerja = hariKerjaDao.findByTanggal(currentDate);
            if (hariKerja.isPresent() && !hariKerja.get().getStatusHari().getId().equals(AppConstant.HARI_LIBUR.getId())) {

                String status = getStatusKehadiran(pegawai, currentDate, false);

                data.setIdPegawai(pegawai.getId());
                data.setNomorAbsen(pegawai.getNomorAbsen() == null ? "" : pegawai.getNomorAbsen());
                data.setNama(Pegawai.toStringNama(pegawai));
                data.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
                data.setTanggal(currentDate);
                data.setWaktuIn(getWaktuInByDateAndPegawai(pegawai, currentDate));
                data.setWaktuOut(getWaktuOutByDateAndPegawai(pegawai, currentDate));
                data.setJabatan(pegawai.getJabatanPegawai() == null ? "" : pegawai.getJabatanPegawai().getNama());
                data.setKeterangan(status);

                datas.add(data);
            }
        }

        return datas;
    }

    private String getWaktuInByDateAndPegawai(Pegawai pegawai, LocalDate waktu) {
        String waktuIn = "";
        Absensi absensi = absensiDao.findByPegawaiAndTanggal(pegawai, waktu);
        if (absensi != null)
            waktuIn = absensi.getWaktuIn() == null ? "" : absensi.getWaktuIn().toString();

        return waktuIn;
    }

    private String getWaktuOutByDateAndPegawai(Pegawai pegawai, LocalDate waktu) {
        String waktuOut = "";
        Absensi absensi = absensiDao.findByPegawaiAndTanggal(pegawai, waktu);
        if (absensi != null)
            waktuOut = absensi.getWaktuOut() == null ? "" : absensi.getWaktuOut().toString();

        return waktuOut;
    }

    private String getStatusKehadiran(Pegawai pegawai, LocalDate waktu, Boolean isPegawaiShift) {
        String status = "";
        Absensi absensi = absensiDao.findByPegawaiAndTanggal(pegawai, waktu);
        if (absensi != null)
            status = absensi.getStatusPresensi().getNama();
        else {
            //status = getStatusKehadiranAlpa(pegawai, waktu, isPegawaiShift);
            status = statusPresensiDao.getOne(AppConstant.STATUS_ALFA.getId()).getNama();
        }

        return status;
    }

    private String getStatusKetidakhadiranByDateAndPegawai(Pegawai pegawai, LocalDate waktu, StatusPresensi statusPresensi, Boolean isPegawaiShift) {

        String status = "";
        Absensi absensi;
        if (statusPresensi == null) {
            absensi = absensiDao.findByPegawaiAndTanggal(pegawai, waktu);
            if (absensi != null) {
                status = absensi.getStatusPresensi().getNama();
            } else {
                status = getStatusKehadiranAlpa(pegawai, waktu, isPegawaiShift);
            }
        }
        if (statusPresensi != null && !statusPresensi.getId().equals(AppConstant.STATUS_ALFA.getId())) {
            absensi = absensiDao.findByPegawaiAndTanggalAndStatusPresensi(pegawai, waktu, statusPresensi);
            if (absensi != null)
                status = absensi.getStatusPresensi().getNama();
        }
        if (statusPresensi != null && statusPresensi.getId().equals(AppConstant.STATUS_ALFA.getId())) {
            status = getStatusKehadiranAlpa(pegawai, waktu, isPegawaiShift);
        }

        return status;
    }

    private String getStatusKehadiranAlpa(Pegawai pegawai, LocalDate waktu, Boolean isPegawaiShift) {
        String status = "";
        int alpa;
        if (isPegawaiShift)
            alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), waktu, waktu)).orElse(0);
        else
            alpa = Optional.ofNullable(hariKerjaDao.hitungJumlahTanpaKeteranganRangeTanggal(pegawai, waktu.getYear(), waktu.getMonthValue(), waktu, waktu)).orElse(0);
        if (alpa > 0)
            status = statusPresensiDao.getOne(AppConstant.STATUS_ALFA.getId()).getNama();

        return status;
    }

    private static Integer getJumlahHari(Double kurangJam) {
        Integer hari = 0;

        if (AbsensiUtils.isBetween(kurangJam, 7.5, 8.1)) {
            hari = 1;
        } else if (AbsensiUtils.isBetween(kurangJam, 8.1, 16.1)) {
            hari = 2;
        } else if (AbsensiUtils.isBetween(kurangJam, 16.1, 24)) {
            hari = 3;
        } else if (kurangJam > 24) {
            if (kurangJam / 8 > 3) {
                hari = (int) (kurangJam / 8);
            } else {
                hari = 3;
            }
        } else {
            hari = 0;
        }

        return hari;
    }

    private static double minutesToHours(int numberOfHours, int numberOfMinutes) {
        int totalMinutes = (numberOfHours * 60) + numberOfMinutes;
        return Math.rint(totalMinutes / 60.0 * 100.0) / 100.0;
    }

    private Duration getExactDuration(LocalDate waktu, LocalDate tglSelesai, Pegawai pegawai, String jenisDurasi) {
        Duration oDuration = Duration.ofHours(0).plusMinutes(0).plusSeconds(0);
        LocalDate tglSelesaiPerBulan = tglSelesai;
        if (waktu.getMonthValue() != tglSelesai.getMonthValue()) {
            YearMonth month = YearMonth.from(waktu);
            tglSelesaiPerBulan = month.atEndOfMonth();
        }
        for (LocalDate tanggalIni = waktu; tanggalIni.isBefore(tglSelesaiPerBulan.plusDays(1)); tanggalIni = tanggalIni.plusDays(1)) {
            LocalTime oLocalTime = LocalTime.of(0, 0);
            switch (jenisDurasi) {
                case AppConstant.TERLAMBAT:
                    oLocalTime = Optional.ofNullable(hariKerjaDao.hitungTerlambatPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                    break;
                case AppConstant.KECEPATAN:
                    oLocalTime = Optional.ofNullable(hariKerjaDao.hitungKecepetanPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                    break;
                case AppConstant.TERLAMBAT_SHIFT:
                    oLocalTime = Optional.ofNullable(hariKerjaDao.hitungTerlambatPegawaiShiftPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                    break;
                case AppConstant.KECEPATAN_SHIFT:
                    oLocalTime = Optional.ofNullable(hariKerjaDao.hitungKecepetanPegawaiShiftPerTanggal(pegawai, tanggalIni)).orElse(LocalTime.of(0, 0, 0));
                    break;
            }
            oDuration = oDuration.plus(Duration.ofHours(oLocalTime.getHour()).plusMinutes(oLocalTime.getMinute()).plusSeconds(oLocalTime.getSecond()));
        }

        return oDuration;
    }
}
