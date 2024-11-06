package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelEmployeeAreaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployeeArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersonnelEmployeeAreaService {

    @Autowired
    private PersonnelEmployeeAreaDao personnelEmployeeAreaDao;

    public PersonnelEmployeeArea findByAreaCodeAndEmployeeId(String code, PersonnelEmployee personnelEmployee) {
        return personnelEmployeeAreaDao.findByAreaIdAreaCodeAndEmployeeId(code, personnelEmployee);
    }

    public void save(PersonnelEmployeeArea personnelEmployeeArea) {
        personnelEmployeeAreaDao.save(personnelEmployeeArea);
    }
}
