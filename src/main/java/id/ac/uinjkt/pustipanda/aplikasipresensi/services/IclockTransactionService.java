package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.IclockTransactionDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IclockTransactionService {
    @Autowired
    private IclockTransactionDao iclockTransactionDao;

    public Optional<IclockTransaction> findById(Integer id) {
        return iclockTransactionDao.findById(id);
    }

    public void save(IclockTransaction iclockTransaction) {
        iclockTransactionDao.save(iclockTransaction);
    }
}
