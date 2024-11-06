package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JamKerjaShiftDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerjaShift;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class JamKerjaShiftService {

    @Autowired
    private JamKerjaShiftDao jamKerjaShiftDao;

    public Page<JamKerjaShift> getFilterBySearchParamAndPage(String nama, Pageable page) {
        return jamKerjaShiftDao.findAll(getSpecificationBySearchParamAndPage(nama), page);
    }

    private Specification<JamKerjaShift> getSpecificationBySearchParamAndPage(String nama) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(nama)) {
                predicates.add(builder.like(builder.lower(root.get("nama")), JpaUtil.getContainsLikePattern(nama)));
            }

            query.orderBy(builder.desc(root.get("id")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public boolean isDataAlreadyExist(JamKerjaShift jamKerjaShift) {
        int i = 0;
        if (jamKerjaShift.getId() == null || jamKerjaShift.getId().isEmpty())
            i = jamKerjaShiftDao.countAllByKode(jamKerjaShift.getKode());
        else
            i = jamKerjaShiftDao.countAllByKodeAndIdNot(jamKerjaShift.getKode(), jamKerjaShift.getId());

        return i > 0;
    }

    public Optional<JamKerjaShift> findById(String id) {
        return jamKerjaShiftDao.findById(id);
    }

    public void save(JamKerjaShift data) {
        jamKerjaShiftDao.save(data);
    }

    public void delete(JamKerjaShift data) {
        jamKerjaShiftDao.delete(data);
    }
}
