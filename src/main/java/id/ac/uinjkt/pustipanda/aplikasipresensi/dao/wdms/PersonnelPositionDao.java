package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelPositionDao extends JpaRepository<PersonnelPosition, Integer> {
}
