package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UnitKerjaPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
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
public class UnitKerjaPresensiService {

    @Autowired
    private UnitKerjaPresensiDao unitKerjaPresensiDao;

    public Optional<UnitKerjaPresensi> getById(Long id) {
        return unitKerjaPresensiDao.findById(id);
    }

    public Page<UnitKerjaPresensi> getAllDataBySearchParam(String name, Unit unit, UnitKerjaPresensi parent, Pageable page) {
        return unitKerjaPresensiDao.findAll(getSpecificationBySearchParam(name, unit, parent), page);
    }

    private Specification<UnitKerjaPresensi> getSpecificationBySearchParam(String name, Unit unit, UnitKerjaPresensi parent) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("nama")), JpaUtil.getContainsLikePattern(name)));
            }

            if (unit != null) {
                predicates.add(builder.equal(root.get("unit"), unit));
            }

            if (parent != null) {
                predicates.add(builder.equal(root.get("parent"), parent));
            }

            query.orderBy(builder.asc(root.get("id")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });

    }

    public Optional<UnitKerjaPresensi> findById(Long id) {
        return unitKerjaPresensiDao.findById(id);
    }

    public void save(UnitKerjaPresensi unitKerjaPresensi) {
        unitKerjaPresensiDao.save(unitKerjaPresensi);
    }

    public void delete(UnitKerjaPresensi unitKerjaPresensi) {
        unitKerjaPresensiDao.delete(unitKerjaPresensi);
    }

    public Optional<UnitKerjaPresensi> findByNamaAndUnitAndParentAndIdNot(String nama, Unit unit, UnitKerjaPresensi parent, Long id) {
        return unitKerjaPresensiDao.findFirstByNamaIgnoreCaseAndParentAndUnitAndIdNot(nama, parent, unit, id);
    }

    public Optional<UnitKerjaPresensi> findByNamaAndUnitAndParent(String nama, Unit unit, UnitKerjaPresensi parent) {
        return unitKerjaPresensiDao.findByNamaIgnoreCaseAndUnitAndParent(nama, unit, parent);
    }

    public List<UnitKerjaPresensi> findAll() {
        return unitKerjaPresensiDao.findAll(Sort.by("id"));
    }
}
