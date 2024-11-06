package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusPresensi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusPresensiDao extends JpaRepository<StatusPresensi, String> {

    @Query(value = "select sp from StatusPresensi sp where sp.id in ('7999-3', '7999-5', '7999-2', '7999-7') order by sp.nama")
    List<StatusPresensi> findAllByFilter();

    @Query(value = "select sp from StatusPresensi sp where sp.id in ('7999-3', '7999-5', '7999-6', '7999-2', '7999-7') order by sp.nama")
    List<StatusPresensi> findAllStatusByFilter();

}
