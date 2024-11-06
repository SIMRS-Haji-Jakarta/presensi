package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelCompanyDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelCompany;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PersonnelCompanyService {

    @Autowired
    private PersonnelCompanyDao personnelCompanyDao;

    public Optional<PersonnelCompany> findById(Integer id) {
        return personnelCompanyDao.findById(id);
    }

    public PersonnelCompany getDefaultCompany() {
        Optional<PersonnelCompany> defaultCompany = findById(AppConstant.UIN_JAKARTA_COMPANY);
        return defaultCompany.orElse(null);
    }
}
