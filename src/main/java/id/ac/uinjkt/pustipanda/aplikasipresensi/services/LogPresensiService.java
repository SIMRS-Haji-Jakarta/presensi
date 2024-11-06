package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.LogPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.LogPresensi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class LogPresensiService {

    @Autowired
    private LogPresensiDao logPresensiDao;

    public void save(LogPresensi logPresensi) {
        logPresensiDao.save(logPresensi);
    }

    public LogPresensi setData(String noAbsen, LocalDateTime waktu, String inOut, Integer verify, String sn, String op, String stamp) {
        LogPresensi logPresensi = new LogPresensi();

        logPresensi.setNoAbsen(noAbsen);
        logPresensi.setWaktu(waktu);
        logPresensi.setStatus(inOut);
        logPresensi.setVerify(verify);
        logPresensi.setSn(sn);
        logPresensi.setOp(op);
        logPresensi.setStamp(stamp);

        return logPresensi;
    }
}
