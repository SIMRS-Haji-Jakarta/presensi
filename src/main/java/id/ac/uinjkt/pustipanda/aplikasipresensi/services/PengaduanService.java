package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PengaduanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengaduan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
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

@Slf4j
@Service
public class PengaduanService {
    @Autowired
    private PengaduanDao pengaduanDao;

    public Page<Pengaduan> getFilterBySearchParamAndPage(String nama, Unit unit, String mulai, String selesai, StatusVerifikasi status, Pageable page) {
        return pengaduanDao.findAll(getSpecificationBySearchParamAndPage(nama, unit, mulai, selesai, status), page);
    }

    private Specification<Pengaduan> getSpecificationBySearchParamAndPage(String nama, Unit unit, String mulai, String selesai, StatusVerifikasi status) {
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

            if (Objects.nonNull(nama)) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), JpaUtil.getContainsLikePattern(nama)));
            }

            query.orderBy(builder.desc(root.get("tanggal")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
