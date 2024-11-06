package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelEmployeeDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersonnelEmployeeService {

    @Autowired
    private PersonnelEmployeeDao personnelEmployeeDao;

    public PersonnelEmployee findByEmpCode(String code) {
        return personnelEmployeeDao.findByEmpCode(code);
    }

    public void save(PersonnelEmployee personnelEmployee) {
        personnelEmployeeDao.save(personnelEmployee);
    }

    public PersonnelEmployee findByIdPegawai(Long id) {
        return personnelEmployeeDao.findByPegawai(id);
    }
}
