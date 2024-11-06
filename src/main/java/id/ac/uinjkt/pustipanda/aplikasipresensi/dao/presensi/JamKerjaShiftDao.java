package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JamKerjaShiftDao extends JpaRepository<JamKerjaShift, String> {
    Page<JamKerjaShift> findAllByNamaContainingIgnoreCase(String nama, Pageable pageable);

    Page<JamKerjaShift> findAll(Specification<JamKerjaShift> specificationBySearchParamAndPage, Pageable page);

    int countAllByKode(String kode);

    int countAllByKodeAndIdNot(String kode, String id);
}
