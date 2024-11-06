package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitKerjaPresensiDao extends JpaRepository<UnitKerjaPresensi, Long> {
    Page<UnitKerjaPresensi> findAll(Specification<UnitKerjaPresensi> specificationBySearchParam, Pageable page);

    Optional<UnitKerjaPresensi> findFirstByNamaIgnoreCaseAndParentAndUnitAndIdNot(String nama, UnitKerjaPresensi parent, Unit unit, Long id);

    Optional<UnitKerjaPresensi> findByNamaIgnoreCaseAndUnitAndParent(String nama, Unit unit, UnitKerjaPresensi unitKerjaPresensi);
}
