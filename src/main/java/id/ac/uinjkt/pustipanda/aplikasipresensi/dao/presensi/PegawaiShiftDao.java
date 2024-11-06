package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PegawaiShiftDao extends JpaRepository<PegawaiShift, String> {
    Page<PegawaiShift> findAllByAdminUnit(AdminUnit adminUnit, Pageable page);

    Page<PegawaiShift> findAllByAdminUnitAndPegawai_NomorAbsenContainingIgnoreCase(AdminUnit adminUnit, String noAbsen, Pageable page);

    Page<PegawaiShift> findAllByAdminUnitAndPegawai(AdminUnit adminUnit, Pegawai pegawai, Pageable page);

    Page<PegawaiShift> findAllByPegawai(Pegawai pegawai, Pageable page);

    Optional<PegawaiShift> findByAdminUnitAndId(AdminUnit adminUnit, String id);

    Optional<PegawaiShift> findByAdminUnitAndPegawai(AdminUnit adminUnit, Pegawai pegawai);

    Optional<PegawaiShift> findFirstByPegawai(Pegawai pegawai);

    Page<PegawaiShift> findAll(Specification<PegawaiShift> specificationBySearchParam, Pageable page);
}
