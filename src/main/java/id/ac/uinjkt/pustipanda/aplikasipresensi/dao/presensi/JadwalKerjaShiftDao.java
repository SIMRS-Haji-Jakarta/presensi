package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JadwalKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface JadwalKerjaShiftDao extends JpaRepository<JadwalKerjaShift, String> {

    Page<JadwalKerjaShift> findAllByTanggalAndPegawaiOrderByTanggalDesc(LocalDate tanggal, Pegawai pegawai, Pageable page);

    Page<JadwalKerjaShift> findAllByTanggalAndJamKerjaShiftAndPegawaiOrderByTanggalDesc(LocalDate tanggal, JamKerjaShift jamKerjaShift, Pegawai pegawai, Pageable page);

    Page<JadwalKerjaShift> findAllByPegawaiOrderByTanggalDesc(Pegawai pegawai, Pageable page);

    @Query("select jks from JadwalKerjaShift jks where jks.adminUnit=:adminUnit and jks.tanggal=:tanggal and jks.pegawai=:pegawai order by jks.tanggal desc")
    Page<JadwalKerjaShift> findAllByAdminUnitAndTanggalAndPegawaiOrderByTanggalDesc(@Param("adminUnit") AdminUnit adminUnit, @Param("tanggal") LocalDate tanggal, @Param("pegawai") Pegawai pegawai, Pageable page);

    @Query("select jks from JadwalKerjaShift jks where jks.adminUnit=:adminUnit and jks.tanggal=:tanggal and jks.jamKerjaShift=:jamKerjaShift and jks.pegawai=:pegawai order by jks.tanggal desc")
    Page<JadwalKerjaShift> findAllByAdminUnitAndTanggalAndJamKerjaShiftAndPegawaiOrderByTanggalDesc(@Param("adminUnit") AdminUnit adminUnit, @Param("tanggal") LocalDate tanggal, @Param("jamKerjaShift") JamKerjaShift jamKerjaShift, @Param("pegawai") Pegawai pegawai, Pageable page);

    @Query("select jks from JadwalKerjaShift jks where jks.adminUnit=:adminUnit and jks.pegawai=:pegawai order by jks.tanggal desc")
    Page<JadwalKerjaShift> findAllByAdminUnitAndPegawaiOrderByTanggalDesc(@Param("adminUnit") AdminUnit adminUnit,@Param("pegawai") Pegawai pegawai, Pageable page);

    @Query("select jks from JadwalKerjaShift jks where jks.adminUnit=:adminUnit order by jks.tanggal desc")
    Page<JadwalKerjaShift> findAllByAdminUnit(@Param("adminUnit") AdminUnit adminUnit, Pageable page);

    @Query("select jks from JadwalKerjaShift jks where jks.adminUnit=:adminUnit and jks.id=:id")
    Optional<JadwalKerjaShift> findByAdminUnitAndId(@Param("adminUnit") AdminUnit adminUnit, @Param("id") String id);

    Optional<JadwalKerjaShift> findByAdminUnitAndPegawaiAndTanggal(AdminUnit adminUnit, Pegawai pegawai, LocalDate tanggal);

    Optional<JadwalKerjaShift> findFirstByPegawaiAndTanggalAndJamKerjaShift(Pegawai pegawai, LocalDate tanggal, JamKerjaShift jamKerjaShift);

    Optional<JadwalKerjaShift> findFirstByPegawaiAndTanggalAndJamKerjaShiftIsNot(Pegawai pegawai, LocalDate tanggal, JamKerjaShift jamKerjaShift);

    Page<JadwalKerjaShift> findAll(Specification<JadwalKerjaShift> specificationBySearchParamAndPage, Pageable page);
}
