package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.TransactionPresensi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionPresensiDao extends JpaRepository<TransactionPresensi, Integer> {
    @Query(value = "update public.transaction_presensi set is_sync = true where id_eptransaction = :id", nativeQuery = true)
    void saveManual(@Param("id") Integer id);
}
