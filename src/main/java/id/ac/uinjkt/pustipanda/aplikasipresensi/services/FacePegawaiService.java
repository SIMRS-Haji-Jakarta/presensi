package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.FacePegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FacePegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FacePegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
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
public class FacePegawaiService {

    @Autowired
    private FacePegawaiDao facePegawaiDao;

    public List<FacePegawai> getListDataAll() {
        return facePegawaiDao.findAll();
    }

    public FacePegawai getByPegawai(Pegawai pegawai) {
        return facePegawaiDao.findByPegawaiOrderByPegawaiIdAsc(pegawai);
    }

    public void save(FacePegawai facePegawai) {
        facePegawaiDao.save(facePegawai);
    }

    public List<FacePegawaiDto> findAllFacePegawaiDtoByMesinPresensi(MesinPresensi mesinPresensi) {
        return facePegawaiDao.findAllFacePegawaiDtoByMesinPresensi(mesinPresensi);
    }

    public List<FacePegawaiDto> findAllFacePegawaiDtoByMesinPresensiAndName(MesinPresensi mesinPresensi, String name) {
        return facePegawaiDao.findAllFacePegawaiDtoByMesinPresensiAndName(mesinPresensi, name);
    }

    public List<FacePegawai> findAllByMesinPresensi(MesinPresensi mesinPresensi) {
        return facePegawaiDao.findAllFacePegawaiByMesinPresensi(mesinPresensi);
    }

    public Page<FacePegawai> getFilterByNamaOrUnitAndPage(String nama, Unit unit, Pageable page) {
        return facePegawaiDao.findAll(getSpecificationByNameOrUnit(nama, unit), page);
    }

    private Specification<FacePegawai> getSpecificationByNameOrUnit(String nama, Unit unit) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nama != null && !nama.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), CommonUtils.getContainsLikePattern(nama)));
            }

            if (unit != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi").get("unit"), unit));
            }

            query.orderBy(builder.desc(root.get("updatedAt")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public Optional<FacePegawai> findById(String id) {
        return facePegawaiDao.findById(id);
    }

    public void delete(FacePegawai facePegawai) {
        facePegawaiDao.delete(facePegawai);
    }
}
