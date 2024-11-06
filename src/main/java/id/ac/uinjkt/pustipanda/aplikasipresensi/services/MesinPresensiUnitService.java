package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.MesinPresensiUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MesinPresensiUnitService {

    @Autowired
    private MesinPresensiUnitDao mesinPresensiUnitDao;

    public Page<MesinPresensiUnit> findAllPageMesinPresensiUnit(String nama, Pageable page) {
        Page<MesinPresensiUnit> data;
        if (nama != null)
            data = mesinPresensiUnitDao.findAllByAdminUnit_NamaContainingIgnoreCase(nama, page);
        else
            data = mesinPresensiUnitDao.findAll(page);

        return data;
    }

    public Optional<MesinPresensiUnit> findById(Long id) {
        return mesinPresensiUnitDao.findById(id);
    }

    public void save(MesinPresensiUnit mesinPresensiUnit) {
        mesinPresensiUnitDao.save(mesinPresensiUnit);
    }

    public void delete(MesinPresensiUnit mesinPresensiUnit) {
        mesinPresensiUnitDao.delete(mesinPresensiUnit);
    }

    public boolean isDataAlreadyExist(MesinPresensiUnit mesinPresensiUnit) {
        int i = 0;
        if (mesinPresensiUnit.getId() == null)
            i = mesinPresensiUnitDao.countAllByAdminUnitIdAndMesinPresensiId(mesinPresensiUnit.getAdminUnit().getId(),
                    mesinPresensiUnit.getMesinPresensi().getId());
        else
            i = mesinPresensiUnitDao.countAllByAdminUnitIdAndMesinPresensiIdAndIdNot(mesinPresensiUnit.getAdminUnit().getId(),
                    mesinPresensiUnit.getMesinPresensi().getId(), mesinPresensiUnit.getId());

        return i > 0;
    }

    public List<MesinPresensiUnit> getListByAdminUnit(AdminUnit adminUnit) {
        return mesinPresensiUnitDao.findAllByAdminUnit(adminUnit);
    }
}
