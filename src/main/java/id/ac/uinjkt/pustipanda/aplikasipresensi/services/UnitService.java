package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UnitService {

    @Autowired
    private UnitDao unitDao;

    public Iterable<Unit> findAll() {
        return unitDao.findAll(Sort.by("nama"));
    }

    public Page<Unit> getAllDataBySearchParam(String name, Pageable page) {
        return unitDao.findAll(getSpecificationBySearchParam(name), page);
    }

    private Specification<Unit> getSpecificationBySearchParam(String name) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("nama")), JpaUtil.getContainsLikePattern(name)));
            }

            query.orderBy(builder.asc(root.get("id")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });

    }

    public Optional<Unit> findById(Long id) {
        return unitDao.findById(id);
    }

    public void save(Unit unit) {
        unitDao.save(unit);
    }

    public void delete(Unit unit) {
        unitDao.delete(unit);
    }

    public Optional<Unit> findByUnitNamaAndIdNot(String nama, Long id) {
        return unitDao.findFirstByNamaIgnoreCaseAndIdNot(nama, id);
    }

    public Optional<Unit> findByUnitNama(String nama) {
        return unitDao.findFirstByNamaIgnoreCase(nama);
    }
}
