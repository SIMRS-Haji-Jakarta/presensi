package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JabatanPegawai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JabatanPegawaiDao extends JpaRepository<JabatanPegawai, Long> {
    Page<JabatanPegawai> findAll(Specification<JabatanPegawai> specificationBySearchParam, Pageable page);

    Optional<JabatanPegawai> findFirstByNamaIgnoreCaseAndIdNot(String nama, Long id);

    Optional<JabatanPegawai> findFirstByNamaIgnoreCase(String nama);
}
