package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelAreaDao extends JpaRepository<PersonnelArea, Integer> {
    PersonnelArea findByAreaCode(String code);
}
