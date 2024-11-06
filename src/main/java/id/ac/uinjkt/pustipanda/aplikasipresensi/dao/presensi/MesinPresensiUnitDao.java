package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesinPresensiUnitDao extends JpaRepository<MesinPresensiUnit, Long> {
    Page<MesinPresensiUnit> findAllByAdminUnit_NamaContainingIgnoreCase(String nama, Pageable pageable);

    Integer countAllByAdminUnitIdAndMesinPresensiIdAndIdNot(String idAdminUnit, Long idMesinPresensi, Long id);

    Integer countAllByAdminUnitIdAndMesinPresensiId(String idAdminUnit, Long idMesinPresensi);

    List<MesinPresensiUnit> findAllByAdminUnit(AdminUnit adminUnit);
}
