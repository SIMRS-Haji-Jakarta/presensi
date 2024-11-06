package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitDao extends JpaRepository<Unit, Long> {
    Page<Unit> findAll(Specification<Unit> specificationBySearchParam, Pageable page);

    Optional<Unit> findFirstByNamaIgnoreCaseAndIdNot(String nama, Long id);

    Optional<Unit> findFirstByNamaIgnoreCase(String nama);
}
