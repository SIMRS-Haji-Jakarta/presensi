package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IclockTransactionDao extends JpaRepository<IclockTransaction, Integer> {
}
