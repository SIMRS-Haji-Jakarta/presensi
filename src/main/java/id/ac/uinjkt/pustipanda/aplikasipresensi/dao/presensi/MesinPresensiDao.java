package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MesinPresensiDao extends JpaRepository<MesinPresensi, Long>, JpaSpecificationExecutor<MesinPresensi> {
    int countAllByKodeArea(Integer kodeArea);

    int countAllByKodeAreaAndIdNot(Integer kodeArea, Long id);

    MesinPresensi findBySn(String sn);

    List<MesinPresensi> findAllByNamaContainingIgnoreCase(String name);
}
