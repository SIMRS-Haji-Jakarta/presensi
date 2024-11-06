package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.StatusPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusPegawai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusPegawaiSevice {
    @Autowired
    private StatusPegawaiDao statusPegawaiDao;

    public List<StatusPegawai> findAll() {
        return statusPegawaiDao.findAll();
    }
}
