package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.KehadiranDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Absensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AbsensiDao extends JpaRepository<Absensi, String>, JpaSpecificationExecutor<Absensi> {
    Absensi findByPegawaiAndTanggal(Pegawai pegawai, LocalDate tanggal);

    Absensi findByPegawaiAndTanggalAndStatusPresensi(Pegawai pegawai, LocalDate tanggal, StatusPresensi statusPresensi);

    Page<Absensi> findAllByOrderByTanggalDesc(Pageable page);

    Page<Absensi> findAllByTanggalAndPegawaiNamaContainingIgnoreCase(LocalDate tanggal, String nama, Pageable page);

    Page<Absensi> findAllByTanggal(LocalDate tanggal, Pageable page);

    Page<Absensi> findAllByPegawaiNamaContainingIgnoreCaseOrderByTanggalDesc(String nama, Pageable page);

    @Query(value = "select a from Absensi a where a.pegawai=:pegawai and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan order by a.tanggal desc ")
    List<Absensi> findAllAbsensiPegawaiTahunBulan(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select a from Absensi a where year(a.tanggal)=:tahun and month(a.tanggal)=:bulan order by a.tanggal desc ")
    Set<Absensi> findAllAbsensiByTahunBulan(@Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.KehadiranDto(a.pegawai.nama, a.pegawai.gelarDepan, a.pegawai.gelarBelakang, a.pegawai.nip, a.waktuInLat, a.waktuInLgn, a.pegawai.unitKerjaPresensi) from Absensi a where a.waktuIn is not null and a.tanggal=:tanggal order by a.waktuIn asc")
    Page<KehadiranDto> kehadiranPegawaiAll(@Param("tanggal") LocalDate tanggal, Pageable page);

    //@Query("select count(a) from Absensi a where a.waktuIn is not null and a.tanggal=:tanggal group by a.waktuIn order by a.waktuIn asc")
    int countAllByTanggalAndWaktuInIsNotNull(@Param("tanggal") LocalDate tanggal);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.KehadiranDto(a.pegawai.nama, a.pegawai.gelarDepan, a.pegawai.gelarBelakang, a.pegawai.nip, a.waktuInLat, a.waktuInLgn, a.pegawai.unitKerjaPresensi) from Absensi a where a.waktuIn is not null and a.tanggal=:tanggal and a.pegawai.unitKerjaPresensi=:unitKerjaPresensi order by a.waktuIn asc")
    Page<KehadiranDto> kehadiranPegawaiUnitkerja(@Param("tanggal") LocalDate tanggal, @Param("unitKerjaPresensi") UnitKerjaPresensi unitKerjaPresensi, Pageable page);

    //@Query("select count(a) from Absensi a where a.waktuIn is not null and a.tanggal=:tanggal and a.pegawai.unitKerjaPresensi=:unitKerjaPresensi group by a.waktuIn order by a.waktuIn asc")
    int countAllByTanggalAndPegawaiUnitKerjaPresensiAndWaktuInIsNotNull(LocalDate tanggal, UnitKerjaPresensi unitKerjaPresensi);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null  and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahHadir(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a join HariKerja h on h.tanggal = a.tanggal where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null  and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and h.statusHari.kode='M'")
    Integer hitungJumlahHadirHariKerjaMasuk(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null  and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai")
    Integer hitungJumlahHadirRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai and hk.statusHari.kode='M'")
    Integer hitungJumlahHadirRangeTanggalHariKerjaMasuk(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and hk.statusHari.kode='M'")
    Integer hitungJumlahHadirByYearAndMonthAndPegawai(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal join JadwalKerjaShift j on a.tanggal = j.tanggal and a.pegawai = j.pegawai where a.pegawai=:pegawai and a.waktuIn is not null and a.waktuOut is not null and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai and j.jamKerjaShift.kode != 'L'")
    Integer hitungJumlahHadirPegawaiShiftRangeTanggalHariKerja(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and a.waktuIn is not null and a.waktuOut is not null  and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahPresensiByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahPresensiTidakMasukByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and a.waktuIn is not null and a.waktuOut is not null  and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai")
    Integer hitungJumlahPresensiRangeTanggalByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai")
    Integer hitungJumlahPresensiTidakMasukRangeTanggalByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahPresensiTidakMasukByPegawaiAndStatusAndYearAndMonth(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);


    @Query(value = "select count(a) from Absensi a join JadwalKerjaShift j on a.tanggal = j.tanggal and a.pegawai = j.pegawai where a.pegawai=:pegawai and a.statusPresensi=:status and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai and j.jamKerjaShift.kode != 'L'")
    Integer hitungJumlahPresensiTidakMasukPegawaiShiftRangeTanggalByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and (a.waktuIn is null or a.waktuOut is null) and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahTidakPresensi(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal where a.pegawai=:pegawai and hk.statusHari.kode='M' and (a.waktuIn is null or a.waktuOut is null) and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan")
    Integer hitungJumlahTidakPresensiHariMasuk(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and (a.waktuIn is null or a.waktuOut is null) and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai")
    Integer hitungJumlahTidakPresensiRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal where a.pegawai=:pegawai and (a.waktuIn is null or a.waktuOut is null) and a.statusPresensi.id not in ('7999-2','7999-3','7999-5','7999-7') and hk.statusHari.kode='M' and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai")
    Integer hitungJumlahTidakPresensiHariMasukRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a join HariKerja hk on a.tanggal = hk.tanggal join JadwalKerjaShift j on a.tanggal = j.tanggal and a.pegawai = j.pegawai where a.pegawai=:pegawai and (a.waktuIn is null or a.waktuOut is null) and a.statusPresensi.id not in ('7999-2','7999-3','7999-5','7999-7') and year(a.tanggal)=:tahun and month(a.tanggal)=:bulan and a.tanggal between :tglMulai and :tglSelesai and j.jamKerjaShift.kode != 'L'")
    Integer hitungJumlahTidakPresensiHariKerjaPegawaiShiftRangeTanggal(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, @Param("bulan") Integer bulan, @Param("tglMulai") LocalDate tglMulai, @Param("tglSelesai") LocalDate tglSelesai);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and a.waktuIn is not null and a.waktuOut is not null  and a.tanggal = :tanggal")
    Integer hitungJumlahPresensiPerTanggalByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tanggal") LocalDate tanggal);

    @Query(value = "select count(a) from Absensi a where a.pegawai=:pegawai and a.statusPresensi=:status and a.tanggal = :tanggal")
    Integer hitungJumlahPresensiTidakMasukPerTanggalByStatus(@Param("pegawai") Pegawai pegawai, @Param("status") StatusPresensi status, @Param("tanggal") LocalDate tanggal);

    Optional<Absensi> findAllByPegawaiAndTanggalOrderById(Pegawai pegawai, LocalDate tanggal);

    @Query(value = "select sum(a.waktuOut - a.waktuIn) from Absensi a join HariKerja h on h.tanggal = a.tanggal where a.pegawai = :pegawai and a.waktuIn is not null and a.waktuOut is not null and a.waktuOut > a.waktuIn and h.statusHari.kode = 'M' and a.tanggal = :tanggal")
    LocalTime hitungJumlahJamKerjaPerTanggal(@Param("pegawai") Pegawai pegawai, @Param("tanggal") LocalDate tanggal);
}
