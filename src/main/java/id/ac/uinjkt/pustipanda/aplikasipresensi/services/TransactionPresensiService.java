package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.TransactionPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.TransactionPresensi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionPresensiService {

    @Autowired
    private TransactionPresensiDao transactionPresensiDao;

    public List<TransactionPresensi> findAll() {
        return transactionPresensiDao.findAll();
    }
}
