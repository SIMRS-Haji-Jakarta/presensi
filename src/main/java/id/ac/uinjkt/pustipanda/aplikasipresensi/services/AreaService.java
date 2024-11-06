package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AreaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Area;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AreaService {

    @Autowired
    private AreaDao areaDao;

    public List<Area> getAreaList(){
        return areaDao.findAllByOrderByIdAsc();
    }
}
