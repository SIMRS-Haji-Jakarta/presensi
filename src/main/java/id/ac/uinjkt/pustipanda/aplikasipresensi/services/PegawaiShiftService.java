package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PegawaiShiftService {

    @Autowired
    private PegawaiShiftDao pegawaiShiftDao;


    public Page<PegawaiShift> getFilterBySearchParamAndPage(String nama, UnitKerjaPresensi unitKerjaPresensi, Pageable page) {
        return pegawaiShiftDao.findAll(getSpecificationBySearchParam(nama, unitKerjaPresensi), page);
    }

    private Specification<PegawaiShift> getSpecificationBySearchParam(String nama, UnitKerjaPresensi unitKerjaPresensi) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nama != null && !nama.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), CommonUtils.getContainsLikePattern(nama)));
            }

            if (unitKerjaPresensi != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi"), unitKerjaPresensi));
            }

            query.orderBy(builder.desc(root.get("updatedAt")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void save(PegawaiShift data) {
        pegawaiShiftDao.save(data);
    }

    public void delete(PegawaiShift data) {
        pegawaiShiftDao.delete(data);
    }

    public Optional<PegawaiShift> findById(String id) {
        return pegawaiShiftDao.findById(id);
    }
}
