package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutAdminUnitDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.HariKerja;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JenisPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusHari;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HariKerjaDao extends PagingAndSortingRepository<HariKerja, String> {
    @Query(value = "select h from HariKerja h where year(h.tanggal)=:tahun and month(h.tanggal)=:bulan order by h.tanggal asc")
    Page<HariKerja> findByTahunBulan(@Param("tahun") Integer tahun, @Param("bulan") Integer bulan, Pageable page);

    Optional<HariKerja> findByTanggal(LocalDate tanggal);

    @Query(value = "select h from HariKerja h join JadwalKerjaShift j on j.tanggal = h.tanggal where j.pegawai = :pegawai and h.tanggal = :tanggal and j.jamKerjaShift.kode != null and j.jamKerjaShift.kode != 'L'")
    Optional<HariKerja> findByTanggalAndPegawaiShift(LocalDate tanggal, Pegawai pegawai);

    @Query("select h from HariKerja h where h.tanggal=:tanggal and h.statusHari=:status")
    Optional<HariKerja> findByTanggalByStatus(@Param("tanggal") LocalDate tanggal, @Param("status") StatusHari statusHari);

    Long deleteByTanggal(LocalDate tanggal);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutDto(hk.tanggal, " +
            " (select x.waktuIn from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai), " +
            " (select x.waktuOut from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai), " +
            " (select y.nama from Absensi x join StatusPresensi y on x.statusPresensi=y.id where x.tanggal=hk.tanggal and x.pegawai=:pegawai), " +
            " (select j.jamKerjaShift.kode from JadwalKerjaShift j where j.tanggal=hk.tanggal and j.pegawai = :pegawai), hk.statusHari.nama) from HariKerja hk where hk.tanggal between :mulai and :selesai order by hk.tanggal asc")
    List<InOutDto> findAllByPeriode(@Param("pegawai") Pegawai pegawai, @Param("mulai") LocalDate mulai, @Param("selesai") LocalDate selesai);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutDto(hk.tanggal, " +
            " (select x.waktuIn from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai), " +
            " (select x.waktuOut from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai), " +
            " (select y.nama from Absensi x join StatusPresensi y on x.statusPresensi=y.id where x.tanggal=hk.tanggal and x.pegawai=:pegawai)," +
            " (select j.jamKerjaShift.kode from JadwalKerjaShift j where j.tanggal=hk.tanggal and j.pegawai = :pegawai), hk.statusHari.nama) from HariKerja hk where hk.tanggal=:tanggal")
    List<InOutDto> findAllByCurrent(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan ")
    Integer hitungHariKerja(@Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai")
    Integer hitungHariKerjaRangeTanggal(@Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(h) from HariKerja h join JadwalKerjaShift j on j.tanggal = h.tanggal where j.pegawai = :pegawai and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and j.jamKerjaShift.kode != null and j.jamKerjaShift.kode != 'L'")
    Integer hitungHariJadwalPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal not in(select a.tanggal from Absensi a where a.pegawai=:pegawai and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan) ")
    Integer hitungJumlahTanpaKeterangan(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and h.tanggal not in(select a.tanggal from Absensi a where a.pegawai=:pegawai and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai)")
    Integer hitungJumlahTanpaKeteranganRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(h) from HariKerja h join JadwalKerjaShift jk on h.tanggal = jk.tanggal where jk.pegawai = :pegawai and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and jk.jamKerjaShift.kode != 'L' and h.tanggal not in(select a.tanggal from Absensi a join JadwalKerjaShift j on a.tanggal = j.tanggal and a.pegawai = j.pegawai where a.pegawai=:pegawai and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai and j.jamKerjaShift.kode != 'L')")
    Integer hitungJumlahTanpaKeteranganPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and h.tanggal in(select j.tanggal from JadwalKerjaShift j where j.pegawai=:pegawai and j.jamKerjaShift.kode='L' and year(j.tanggal)=:tahun and month(j.tanggal)=:bulan and j.tanggal between :tglMulai and :tglSelesai)")
    Integer hitungHariLiburPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(h) from HariKerja h where h.statusHari.kode='M' and h.tanggal=:tanggal and h.tanggal not in(select a.tanggal from Absensi a where a.pegawai=:pegawai and a.tanggal=:tanggal) ")
    Integer hitungJumlahTanpaKeteranganPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select sum(a.waktuIn - h.jamKerja.datang) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuIn > h.jamKerja.datang and h.statusHari.kode='M' and h.tanggal = :tanggal and a.pegawai=:pegawai ")
    LocalTime hitungTerlambatPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select sum(a.waktuIn - h.jamKerja.datang) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuIn > h.jamKerja.datang and h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and a.pegawai=:pegawai ")
    LocalTime hitungTerlambatRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select sum(a.waktuIn - h.jamKerja.datang) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuIn > h.jamKerja.datang and h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and a.pegawai=:pegawai ")
    LocalTime hitungTerlambatByPegawaiAndYearAndMonth(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select sum(a.waktuIn - j.jamKerjaShift.datang) from JadwalKerjaShift j join HariKerja h on h.tanggal = j.tanggal join Absensi a on j.tanggal=a.tanggal and a.pegawai=j.pegawai where a.waktuIn > j.jamKerjaShift.datang and h.tanggal = :tanggal and a.pegawai=:pegawai and j.jamKerjaShift.kode != 'L'")
    LocalTime hitungTerlambatPegawaiShiftPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select sum(a.waktuIn - j.jamKerjaShift.datang) from JadwalKerjaShift j join HariKerja h on h.tanggal = j.tanggal join Absensi a on j.tanggal=a.tanggal and a.pegawai=j.pegawai where a.waktuIn > j.jamKerjaShift.datang and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and a.pegawai=:pegawai and j.jamKerjaShift.kode != 'L'")
    LocalTime hitungTerlambatPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select sum(h.jamKerja.pulang - a.waktuOut) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuOut < h.jamKerja.pulang and h.statusHari.kode='M' and h.tanggal = :tanggal and a.pegawai=:pegawai ")
    LocalTime hitungKecepetanPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select sum(h.jamKerja.pulang - a.waktuOut) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuOut < h.jamKerja.pulang and h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and a.pegawai=:pegawai ")
    LocalTime hitungKecepetanRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select sum(h.jamKerja.pulang - a.waktuOut) from HariKerja h join Absensi a on h.tanggal=a.tanggal where a.waktuOut < h.jamKerja.pulang and h.statusHari.kode='M' and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and a.pegawai=:pegawai ")
    LocalTime hitungKecepetanByPegawaiAndYearAndMonth(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select sum(j.jamKerjaShift.pulang - a.waktuOut) from JadwalKerjaShift j join HariKerja h on h.tanggal = j.tanggal join Absensi a on j.tanggal=a.tanggal and a.pegawai=j.pegawai where a.waktuOut < j.jamKerjaShift.pulang and h.tanggal = :tanggal and a.pegawai=:pegawai and j.jamKerjaShift.kode != 'L'")
    LocalTime hitungKecepetanPegawaiShiftPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select sum(j.jamKerjaShift.pulang - a.waktuOut) from JadwalKerjaShift j join HariKerja h on h.tanggal = j.tanggal join Absensi a on j.tanggal=a.tanggal and a.pegawai=j.pegawai where a.waktuOut < j.jamKerjaShift.pulang and year(h.tanggal)=:tahun and month(h.tanggal)=:bulan and h.tanggal between :tglMulai and :tglSelesai and a.pegawai=:pegawai and j.jamKerjaShift.kode != 'L'")
    LocalTime hitungKecepetanPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutDto(hk.tanggal, " +
            " (select x.waktuIn from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai and x.pegawai.jenisPegawai=:jenisPegawai), " +
            " (select x.waktuOut from Absensi x where x.tanggal=hk.tanggal and x.pegawai=:pegawai and x.pegawai.jenisPegawai=:jenisPegawai), " +
            " (select y.nama from Absensi x join StatusPresensi y on x.statusPresensi=y.id where x.tanggal=hk.tanggal and x.pegawai=:pegawai and x.pegawai.jenisPegawai=:jenisPegawai), " +
            " (select j.jamKerjaShift.kode from JadwalKerjaShift j where j.tanggal=hk.tanggal and j.pegawai = :pegawai), hk.statusHari.nama) from HariKerja hk where hk.tanggal between :mulai and :selesai order by hk.tanggal asc")
    List<InOutDto> findAllByPeriodeAndJenisPegawai(@Param("pegawai") Pegawai pegawai, @Param("jenisPegawai") JenisPegawai jenisPegawai, @Param("mulai") LocalDate mulai, @Param("selesai") LocalDate selesai);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutAdminUnitDto(hk.tanggal, " +
            " (select x.waktuIn from Absensi x where x.tanggal=hk.tanggal and x.pegawai = :pegawai), " +
            " (select x.waktuOut from Absensi x where x.tanggal=hk.tanggal and x.pegawai = :pegawai), " +
            " (select x.pegawai from Absensi x where x.tanggal=hk.tanggal and x.pegawai = :pegawai), " +
            " (select y.nama from Absensi x join StatusPresensi y on x.statusPresensi=y.id where x.tanggal=hk.tanggal and x.pegawai = :pegawai), " +
            " (select j.jamKerjaShift.kode from JadwalKerjaShift j where j.tanggal=hk.tanggal and j.pegawai = :pegawai), hk.statusHari.nama) from HariKerja hk where hk.tanggal between :mulai and :selesai order by hk.tanggal asc")
    List<InOutAdminUnitDto> findAllByPegawaiAndPeriode(@Param("pegawai") Pegawai pegawai, @Param("mulai") LocalDate mulai, @Param("selesai") LocalDate selesai);

    List<HariKerja> findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualOrderByTanggalAsc(LocalDate tanggalMulai, LocalDate tanggalSelesai);

    @Query(value = "select count(h.tanggal) from HariKerja h where month(h.tanggal)=:intBulan and h.tanggal between :mulai and :selesai")
    Long hitungJumlahHariPerBulan(@Param("intBulan") Integer intBulan, @Param("mulai") LocalDate mulai, @Param("selesai") LocalDate selesai);
}
