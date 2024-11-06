package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.EpEptransactionDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.EpEptransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EpEptransactionService {

    @Autowired
    private EpEptransactionDao epEptransactionDao;

    public Optional<EpEptransaction> findById(Integer id) {
        return epEptransactionDao.findById(id);
    }

    public void save(EpEptransaction epEptransaction) {
        epEptransactionDao.save(epEptransaction);
    }
}
