package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.GolonganDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Golongan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GolonganSevice {
    @Autowired
    private GolonganDao golonganDao;

    public List<Golongan> findAll() {
        return golonganDao.findAll();
    }
}
