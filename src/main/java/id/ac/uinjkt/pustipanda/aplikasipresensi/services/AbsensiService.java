package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AbsensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Absensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.KategoriPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AbsensiService {

    @Autowired
    private AbsensiDao absensiDao;

    public Absensi getAbsensi(Pegawai pegawai, LocalDate localDate) {
        return absensiDao.findByPegawaiAndTanggal(pegawai, localDate);
    }

    public Absensi setDataWaktuIn(Absensi absensi, LocalDate localDate, LocalTime waktuIn) {
        absensi.setWaktuIn(waktuIn);
        absensi.setWaktuInIp(AppConstant.DEFAULT_IP);
        absensi.setWaktuInLat(AppConstant.DEFAULT_LATITUDE);
        absensi.setWaktuInLgn(AppConstant.DEFAULT_LONGITUDE);
        absensi.setTanggal(localDate);
        absensi.setWaktuOutIp(AppConstant.DEFAULT_IP);
        absensi.setWaktuOutLat(AppConstant.DEFAULT_LATITUDE);
        absensi.setWaktuOutLgn(AppConstant.DEFAULT_LONGITUDE);
        absensi.setStatusPresensi(AppConstant.STATUS_HADIR);

        return absensi;
    }

    public Absensi setDataWaktuOut(Absensi absensi, LocalDate localDate, LocalTime waktuOut) {
        absensi.setWaktuOut(waktuOut);
        absensi.setWaktuInIp(AppConstant.DEFAULT_IP);
        absensi.setWaktuInLat(AppConstant.DEFAULT_LATITUDE);
        absensi.setWaktuInLgn(AppConstant.DEFAULT_LONGITUDE);
        absensi.setTanggal(localDate);
        absensi.setWaktuOutIp(AppConstant.DEFAULT_IP);
        absensi.setWaktuOutLat(AppConstant.DEFAULT_LATITUDE);
        absensi.setWaktuOutLgn(AppConstant.DEFAULT_LONGITUDE);
        absensi.setStatusPresensi(AppConstant.STATUS_HADIR);

        return absensi;
    }

    public void save(Absensi absensi) {
        absensiDao.save(absensi);
    }

    public Page<Absensi> getPageFilterByTanggalAndPegawaiOrUnitOrKategori(LocalDate tanggal, Pegawai pegawai, Unit unit, KategoriPegawai kategoriPegawai, Pageable page) {
        return absensiDao.findAll(getSpecificationByTanggalAndPegawaiOrUnitOrKategori(tanggal, pegawai, unit, kategoriPegawai), page);
    }

    private Specification<Absensi> getSpecificationByTanggalAndPegawaiOrUnitOrKategori(LocalDate tanggal, Pegawai pegawai, Unit unit, KategoriPegawai kategoriPegawai) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (pegawai != null) {
                predicates.add(builder.equal(root.get("pegawai"), pegawai));
            }

            if (unit != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi").get("unit"), unit));
            }

            if (kategoriPegawai != null) {
                predicates.add(builder.equal(root.get("pegawai").get("jenisPegawai").get("kategoriPegawai"), kategoriPegawai));
            }

            predicates.add(builder.equal(root.get("tanggal"), tanggal));
            predicates.add(builder.isNotNull(root.get("waktuIn")));

            query.orderBy(builder.asc(root.get("waktuIn")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
