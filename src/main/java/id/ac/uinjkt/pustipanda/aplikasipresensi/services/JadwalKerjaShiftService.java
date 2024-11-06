package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JadwalKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class JadwalKerjaShiftService {

    @Autowired
    private JadwalKerjaShiftDao jadwalKerjaShiftDao;

    public Page<JadwalKerjaShift> getFilterBySearchParamAndPage(String nama, LocalDate tanggal, JamKerjaShift jamKerjaShift, UnitKerjaPresensi unitKerjaPresensi, Pageable page) {
        return jadwalKerjaShiftDao.findAll(getSpecificationBySearchParamAndPage(nama, tanggal, jamKerjaShift, unitKerjaPresensi), page);
    }

    private Specification<JadwalKerjaShift> getSpecificationBySearchParamAndPage(String nama, LocalDate tanggal, JamKerjaShift jamKerjaShift, UnitKerjaPresensi unitKerjaPresensi) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(nama)) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), JpaUtil.getContainsLikePattern(nama)));
            }

            if (tanggal != null) {
                predicates.add(builder.equal(root.get("tanggal"), tanggal));
            }

            if (jamKerjaShift != null) {
                predicates.add(builder.equal(root.get("jamKerjaShift"), jamKerjaShift));
            }

            if (unitKerjaPresensi != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi"), unitKerjaPresensi));
            }

            query.orderBy(builder.desc(root.get("id")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public Optional<JadwalKerjaShift> findById(String id) {
        return jadwalKerjaShiftDao.findById(id);
    }

    public void save(JadwalKerjaShift data) {
        jadwalKerjaShiftDao.save(data);
    }

    public void delete(JadwalKerjaShift data) {
        jadwalKerjaShiftDao.delete(data);
    }

    public Optional<JadwalKerjaShift> findByAdminUnitAndPegawaiAndTanggal(AdminUnit adminUnit, Pegawai pegawai, LocalDate tanggal) {
        return jadwalKerjaShiftDao.findByAdminUnitAndPegawaiAndTanggal(adminUnit, pegawai, tanggal);
    }
}
