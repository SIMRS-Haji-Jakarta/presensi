package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelEmployeeDao extends JpaRepository<PersonnelEmployee, Integer> {
    PersonnelEmployee findByEmpCode(String code);

    PersonnelEmployee findByPegawai(Long id);
}
