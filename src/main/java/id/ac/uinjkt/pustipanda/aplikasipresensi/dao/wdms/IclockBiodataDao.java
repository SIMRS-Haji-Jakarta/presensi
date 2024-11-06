package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockBiodata;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IclockBiodataDao extends JpaRepository<IclockBiodata, Integer> {
    List<IclockBiodata> findAllByEmployeeId_DepartmentId_IdAndValid(int idDepartment, int isValid);

    IclockBiodata findFirstByEmployeeId(PersonnelEmployee personnelEmployee);
}
