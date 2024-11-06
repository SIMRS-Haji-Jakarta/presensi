package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonnelDepartmentDao extends JpaRepository<PersonnelDepartment, Integer> {
    Optional<PersonnelDepartment> findByDeptCode(String code);
}
