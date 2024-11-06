package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployeeArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelEmployeeAreaDao extends JpaRepository<PersonnelEmployeeArea, Integer> {
    PersonnelEmployeeArea findByAreaIdAreaCodeAndEmployeeId(String code, PersonnelEmployee personnelEmployee);
}
