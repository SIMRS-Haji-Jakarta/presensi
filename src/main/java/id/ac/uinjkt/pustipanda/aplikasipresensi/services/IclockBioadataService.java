package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.IclockBiodataDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockBiodata;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IclockBioadataService {

    @Autowired
    private IclockBiodataDao iclockBiodataDao;

    public List<IclockBiodata> findListAllValidByMesinPresensiCode(Integer kodeArea) {
        return iclockBiodataDao.findAllByEmployeeId_DepartmentId_IdAndValid(kodeArea, 1);
    }

    public IclockBiodata findFirstByEmployeeId(PersonnelEmployee personnelEmployee) {
        return iclockBiodataDao.findFirstByEmployeeId(personnelEmployee);
    }

    public void save(IclockBiodata iclockBiodata) {
        iclockBiodataDao.save(iclockBiodata);
    }

    public List<IclockBiodata> findAll() {
        return iclockBiodataDao.findAll();
    }
}
