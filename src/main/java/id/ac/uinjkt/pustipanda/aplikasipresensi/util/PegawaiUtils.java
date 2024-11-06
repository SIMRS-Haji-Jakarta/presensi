package id.ac.uinjkt.pustipanda.aplikasipresensi.util;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PegawaiUtils {
    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private PegawaiShiftDao pegawaiShiftDao;

    public List<Long> generateListUnitKerjaAdminUnit(Pegawai pegawai) {
        //log.info("pegawai: {}", pegawai);

        AdminUnit adminUnit = adminUnitDao.findByPegawai(pegawai);
        //log.info("adminUnit : {}", adminUnit);

        List<UnitKerjaPresensi> unitKerjas = new ArrayList<>(adminUnit.getUnitKerja());

        List<Long> result = new ArrayList<>();
        for (UnitKerjaPresensi tess : unitKerjas) {
            result.add(tess.getId());
        }
        return result;
    }

    public Boolean isExistPegawaiShiftAdminUnit(AdminUnit adminUnit, Pegawai pegawai) {

        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findByAdminUnitAndPegawai(adminUnit, pegawai);
        return pegawaiShift.isPresent();
    }

    public Boolean isPegawaiShift(Pegawai pegawai) {

        Optional<PegawaiShift> pegawaiShift = pegawaiShiftDao.findFirstByPegawai(pegawai);
        return pegawaiShift.isPresent();
    }

}
