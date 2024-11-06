package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.wdms.PersonnelPositionDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PersonnelPositionService {

    @Autowired
    private PersonnelPositionDao personnelPositionDao;

    @Autowired
    private JenisPegawaiService jenisPegawaiService;

    public PersonnelPosition getDataByPegawai(Pegawai pegawai) {
        Optional<PersonnelPosition> data;
        boolean idDosen = jenisPegawaiService.isJenisPegawaiDosen(pegawai);
        if (idDosen)
            data = findById(AppConstant.PERSONNEL_POSITION_DOSEN);
        else
            data = findById(AppConstant.PERSONNEL_POSITION_TENDIK);

        return data.orElse(null);
    }

    private Optional<PersonnelPosition> findById(Integer id) {
        return personnelPositionDao.findById(id);
    }
}
