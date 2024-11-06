package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AbsensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.HariKerjaDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PengajuanService {
    private final static String ROUTE_FILE_PENGAJUAN = "/file-pengajuan/";

    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private AbsensiDao absensiDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @Autowired
    private FileStorageService fileStorageService;

    public Page<Pengajuan> getFilterBySearchParamAndPage(String nama, Unit unit, String mulai, String selesai, StatusVerifikasi status, UnitKerjaPresensi unitKerjaPresensi, Pageable page) {
        return pengajuanDao.findAll(getSpecificationBySearchParamAndPage(nama, unit, mulai, selesai, status, unitKerjaPresensi), page);
    }

    private Specification<Pengajuan> getSpecificationBySearchParamAndPage(String nama, Unit unit, String mulai, String selesai, StatusVerifikasi status, UnitKerjaPresensi unitKerjaPresensi) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (unit != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi").get("unit"), unit));
            }

            if (mulai != null && !mulai.isEmpty()) {
                LocalDate tglMulai = LocalDate.parse(mulai);
                predicates.add(builder.greaterThanOrEqualTo(root.get("tanggalMulai"), tglMulai));
            }

            if (selesai != null && !selesai.isEmpty()) {
                LocalDate tglSelesai = LocalDate.parse(selesai);
                predicates.add(builder.lessThanOrEqualTo(root.get("tanggalSelesai"), tglSelesai));
            }

            if (status != null) {
                predicates.add(builder.equal(root.get("statusVerifikasi"), status));
            }

            if (unitKerjaPresensi != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi"), unitKerjaPresensi));
            }

            if (Objects.nonNull(nama)) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), JpaUtil.getContainsLikePattern(nama)));
            }

            query.orderBy(builder.desc(root.get("tanggal")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    //@Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 0 23 * * *")
    public void generatePengajuan() {
        log.info("Cek pengajuan");

        List<Pengajuan> pengajuans = pengajuanDao.findAllByStatusVerifikasiAndStatusProsesFalseOrderByTanggalDesc(AppConstant.STATUS_DITERIMA);

        for (Pengajuan p : pengajuans) {
            //log.info("pengajuan: {}", p);

            /* ngetes manual
            if (p.getPegawai().getId() == 1167L) {
                log.info("pengajuan pegawai : {}", p);

                long jumlahTanggal = 0;
                if (p.getTanggalMulai().isEqual(p.getTanggalSelesai())) {
                    jumlahTanggal = 1;
                } else {
                    jumlahTanggal = ChronoUnit.DAYS.between(p.getTanggalMulai(), p.getTanggalSelesai().plusDays(1));
                }

                log.info("jumlahTanggal: {}", jumlahTanggal);

                List<LocalDate> tanggals = Stream.iterate(p.getTanggalMulai(), date -> date.plusDays(1))
                        .limit(jumlahTanggal)
                        .collect(Collectors.toList());

                for (LocalDate ld : tanggals) {
                    log.info("Tanggal : {}", ld);
                }
            }
            */

            prosesPengajuan(p);
        }
    }

    public void resetPengajuan(String id) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent()) {
            pengajuan.get().setStatusProses(false);
            pengajuan.get().setStatusVerifikasi(AppConstant.STATUS_DIAJUKAN);

            List<LocalDate> tanggals = getListTanggalFromPengajuan(pengajuan.get());

            for (LocalDate ld : tanggals) {
                Absensi absensi = absensiDao.findByPegawaiAndTanggal(pengajuan.get().getPegawai(), ld);
                if (absensi != null) {
                    log.info("Reset pengajuan: Absensi {} ini dihapus untuk keperluan proses pengajuan ketidakhadiran berikutnya", absensi.getId());
                    absensiDao.delete(absensi);
                }
            }

            pengajuanDao.save(pengajuan.get());
        }
    }

    private static List<LocalDate> getListTanggalFromPengajuan(Pengajuan pengajuan) {
        long jumlahTanggal;
        if (pengajuan.getTanggalMulai().isEqual(pengajuan.getTanggalSelesai())) {
            jumlahTanggal = 1;
        } else {
            jumlahTanggal = ChronoUnit.DAYS.between(pengajuan.getTanggalMulai(), pengajuan.getTanggalSelesai().plusDays(1));
        }

        return Stream.iterate(pengajuan.getTanggalMulai(), date -> date.plusDays(1))
                .limit(jumlahTanggal)
                .collect(Collectors.toList());
    }

    public void updatePengajuan(Pengajuan o) {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(o.getId());
        if (pengajuan.isPresent()) {
            pengajuan.get().setCatatan(o.getCatatan());
            pengajuan.get().setStatusPresensi(o.getStatusPresensi());
            pengajuan.get().setStatusVerifikasi(o.getStatusVerifikasi());
            pengajuan.get().setTanggalMulai(o.getTanggalMulai());
            pengajuan.get().setTanggalSelesai(o.getTanggalSelesai());

            log.info("pusat update pengajuan: {}", pengajuan.get());
            pengajuanDao.save(pengajuan.get());
        }
    }

    public ResponseEntity<ByteArrayResource> getFilePengajuan(String id) throws IOException {
        Optional<Pengajuan> pengajuan = pengajuanDao.findById(id);
        if (pengajuan.isPresent()) {
            String file = pengajuan.get().getUrlBukti();

            log.info("pengajuan: {}", pengajuan.get());

            byte[] data = fileStorageService.getFile(file);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                    .body(resource);
        }
        return null;
    }

    public void deletePengajuanById(String id) {
        pengajuanDao.deleteById(id);
    }

    public void prosesPengajuan(Pengajuan p) {
        try {
            List<LocalDate> tanggals = getListTanggalFromPengajuan(p);

            for (LocalDate ld : tanggals) {
                //log.info("Tanggal : {}", ld);

                Optional<HariKerja> oHariKerja = hariKerjaDao.findByTanggalByStatus(ld, AppConstant.HARI_MASUK);
                if (oHariKerja.isPresent()) {
                    Absensi absensi = absensiDao.findByPegawaiAndTanggal(p.getPegawai(), ld);
                    log.info("cek absensi : {}", absensi);

                    if (absensi == null) {
                        absensi = new Absensi();
                        absensi.setTanggal(ld);
                        absensi.setPegawai(p.getPegawai());
                    }

                    absensi.setWaktuIn(null);
                    absensi.setWaktuInIp(AppConstant.DEFAULT_IP);
                    absensi.setWaktuInLat(AppConstant.DEFAULT_LATITUDE);
                    absensi.setWaktuInLgn(AppConstant.DEFAULT_LONGITUDE);
                    absensi.setWaktuOut(null);
                    absensi.setWaktuOutIp(AppConstant.DEFAULT_IP);
                    absensi.setWaktuOutLat(AppConstant.DEFAULT_LATITUDE);
                    absensi.setWaktuOutLgn(AppConstant.DEFAULT_LONGITUDE);
                    absensi.setStatusPresensi(p.getStatusPresensi());

                    log.info("status pengajuan absensi : {}", absensi);
                    absensiDao.save(absensi);
                }
            }
            p.setStatusProses(true);
            pengajuanDao.save(p);
        } catch (Exception e) {
            log.error("error proses: {}", e);
        }
    }

    public String getViewableUrl(Pengajuan pengajuan, String urlBerkas) {
        String objectName;
        if (urlBerkas.isEmpty()) {
            objectName = ROUTE_FILE_PENGAJUAN + pengajuan.getUrlBukti();
        } else
            objectName = urlBerkas;

        return objectName;
    }
}
