package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelAreaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersonnelAreaService {

    @Autowired
    private PersonnelAreaDao personnelAreaDao;

    public PersonnelArea findByAreaCode(String code){
        return personnelAreaDao.findByAreaCode(code);
    }

}
