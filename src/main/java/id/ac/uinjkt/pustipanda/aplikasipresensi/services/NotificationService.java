package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengaduanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengaduan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private PengaduanDao pengaduanDao;

    @Autowired
    private FileStorageService fileStorageService;


    //@Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 29 0 * * *")
    public void removePengajuanLama() throws IOException {
        List<Pengajuan> pengajuans = pengajuanDao.findAllByStatusVerifikasiAndStatusProsesFalseAndTanggalBefore(AppConstant.STATUS_DIAJUKAN, LocalDateTime.now().minusDays(365));
        for (Pengajuan p : pengajuans) {
            log.info("pengajuan : {}", p);

            fileStorageService.deleteFile(p.getUrlBukti());

            pengajuanDao.delete(p);
        }

    }

    //@Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 29 0 * * *")
    public void removePengaduanLama() throws IOException {
        List<Pengaduan> pengaduans = pengaduanDao.findAllByStatusVerifikasiAndTanggalBefore(AppConstant.STATUS_DIAJUKAN, LocalDateTime.now().minusDays(365));
        for (Pengaduan p : pengaduans) {
            log.info("pengaduan : {}", p);

            fileStorageService.deleteFile(p.getUrlBukti());

            pengaduanDao.delete(p);
        }

    }
}
