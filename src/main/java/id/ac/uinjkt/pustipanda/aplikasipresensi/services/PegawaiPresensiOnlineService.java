package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiPresensiOnlineDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiPresensiOnline;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PegawaiPresensiOnlineService {
    @Autowired
    private PegawaiPresensiOnlineDao pegawaiPresensiOnlineDao;

    @Autowired
    private PegawaiService pegawaiService;

    public Page<PegawaiPresensiOnline> getFilterBySearchParamAndPage(String nama, Unit unit, Pageable page) {
        return pegawaiPresensiOnlineDao.findAll(getSpecificationBySearchParamAndPage(nama, unit), page);
    }

    private Specification<PegawaiPresensiOnline> getSpecificationBySearchParamAndPage(String nama, Unit unit) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (unit != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi").get("unit"), unit));
            }

            if (Objects.nonNull(nama)) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), JpaUtil.getContainsLikePattern(nama)));
            }

            query.orderBy(builder.desc(root.get("tanggal")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public boolean isDataAlreadyExist(PegawaiPresensiOnline o) {
        int i = 0;
        if (o.getId() == null || o.getId().isEmpty())
            i = pegawaiPresensiOnlineDao.countAllByPegawai(o.getPegawai());

        return i > 0;
    }

    @Transactional
    public void save(PegawaiPresensiOnline data) {
        pegawaiPresensiOnlineDao.save(data);

        //update ke FALSE
        pegawaiService.setIsFace(data.getPegawai(), false);
    }

    @Transactional
    public void delete(PegawaiPresensiOnline data) {
        pegawaiPresensiOnlineDao.delete(data);

        //update ke TRUE
        pegawaiService.setIsFace(data.getPegawai(), true);
    }

    public Optional<PegawaiPresensiOnline> findById(String id) {
        return pegawaiPresensiOnlineDao.findById(id);
    }
}
