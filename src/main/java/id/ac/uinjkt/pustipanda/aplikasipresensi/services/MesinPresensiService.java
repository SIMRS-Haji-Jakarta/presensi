package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.MesinPresensiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.MesinPresensiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MesinPresensiService {

    @Autowired
    private MesinPresensiDao mesinPresensiDao;

    public Page<MesinPresensi> getFilterByNamaOrKodeAreaAndPage(String nama, String kodeArea, Pageable page) {
        return mesinPresensiDao.findAll(getSpecificationByNameOrKodeArea(nama, kodeArea), page);
    }

    private Specification<MesinPresensi> getSpecificationByNameOrKodeArea(String nama, String kodeArea) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nama != null && !nama.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("nama")), CommonUtils.getContainsLikePattern(nama)));
            }

            if (kodeArea != null && !kodeArea.isEmpty()) {
                predicates.add(builder.equal(root.get("kodeArea"), kodeArea));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }


    public List<MesinPresensi> findListAllMesinPresensi() {
        return mesinPresensiDao.findAll();
    }

    public List<MesinPresensi> getAllByListMesinPresensiUnit(List<MesinPresensiUnit> mesinPresensiUnitList) {
        List<MesinPresensi> mesinPresensis = new LinkedList<>();

        for (MesinPresensiUnit mesinPresensiUnit : mesinPresensiUnitList) {
            mesinPresensis.add(mesinPresensiUnit.getMesinPresensi());
        }

        return mesinPresensis;
    }

    public Optional<MesinPresensi> findById(Long id) {
        return mesinPresensiDao.findById(id);
    }

    public MesinPresensi findBySn(String sn){
        return mesinPresensiDao.findBySn(sn);
    }

    public void save(MesinPresensi mesinPresensi) {
        mesinPresensiDao.save(mesinPresensi);
    }

    public void delete(MesinPresensi mesinPresensi) {
        mesinPresensiDao.delete(mesinPresensi);
    }

    public boolean isDataAlreadyExist(MesinPresensiDto o) {
        int i = 0;
        if (o.getId() == null)
            i = mesinPresensiDao.countAllByKodeArea(o.getKodeArea());
        else
            i = mesinPresensiDao.countAllByKodeAreaAndIdNot(o.getKodeArea(), o.getId());

        return i > 0;
    }

    public List<MesinPresensi> findListAllMesinPresensiRsHaji() {
        return mesinPresensiDao.findAllByNamaContainingIgnoreCase("haji");
    }
}
