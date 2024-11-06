package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusVerifikasiDao extends JpaRepository<StatusVerifikasi, String> {
    @Query(value = "select sv from StatusVerifikasi sv where sv.id in ('7997-1', '7997-2', '7997-4', '7997-5') order by sv.nama")
    List<StatusVerifikasi> findAllByFilter();
}
