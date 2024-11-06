package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelDepartmentDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelDepartment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PersonnelDepartmentService {

    @Autowired
    private PersonnelDepartmentDao personnelDepartmentDao;

    public Optional<PersonnelDepartment> findById(Integer id) {
        return personnelDepartmentDao.findById(id);
    }

    public PersonnelDepartment getDataByUnit(Unit unit) {
        Optional<PersonnelDepartment> data = findById(Math.toIntExact(unit.getId()));
        return data.orElse(null);
    }
}
