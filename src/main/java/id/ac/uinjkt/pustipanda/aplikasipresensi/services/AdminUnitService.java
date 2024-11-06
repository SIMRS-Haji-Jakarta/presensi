package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminUnitService {

    @Autowired
    private AdminUnitDao adminUnitDao;

    public List<AdminUnit> findListAllAdminUnit() {
        return adminUnitDao.findAll();
    }
}
