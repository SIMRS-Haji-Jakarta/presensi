package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUnitDao extends JpaRepository<AdminUnit, String> {
    AdminUnit findByPegawai(Pegawai pegawai);

    Optional<AdminUnit> findByUnitKerja(UnitKerjaPresensi unitKerjaPresensi);

    Page<AdminUnit> findAllByOrderByIdAsc(Pageable page);

    Page<AdminUnit> findAllByPegawaiNamaContainingIgnoreCase(String nama, Pageable page);

    Page<AdminUnit> findAllByNamaContainingIgnoreCase(String nama, Pageable page);

    Page<AdminUnit> findAllByPegawaiOrderByPegawaiId(Pegawai pegawai, Pageable page);
}
