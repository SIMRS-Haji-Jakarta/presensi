package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JabatanPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JabatanPegawai;
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
import java.util.Optional;

@Slf4j
@Service
public class JabatanPegawaiService {

    @Autowired
    private JabatanPegawaiDao jabatanPegawaiDao;

    public Page<JabatanPegawai> getAllDataBySearchParam(String name, Pageable page) {
        return jabatanPegawaiDao.findAll(getSpecificationBySearchParam(name), page);
    }

    private Specification<JabatanPegawai> getSpecificationBySearchParam(String name) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("nama")), JpaUtil.getContainsLikePattern(name)));
            }

            query.orderBy(builder.asc(root.get("id")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });

    }

    public Optional<JabatanPegawai> findById(Long id) {
        return jabatanPegawaiDao.findById(id);
    }

    public void save(JabatanPegawai jabatanPegawai) {
        jabatanPegawaiDao.save(jabatanPegawai);
    }

    public void delete(JabatanPegawai jabatanPegawai) {
        jabatanPegawaiDao.delete(jabatanPegawai);
    }

    public Optional<JabatanPegawai> findByJabatanPegawaiNamaAndIdNot(String nama, Long id) {
        return jabatanPegawaiDao.findFirstByNamaIgnoreCaseAndIdNot(nama, id);
    }

    public Optional<JabatanPegawai> findByJabatanPegawaiNama(String nama) {
        return jabatanPegawaiDao.findFirstByNamaIgnoreCase(nama);
    }

    public List<JabatanPegawai> findAll() {
        return jabatanPegawaiDao.findAll();
    }
}
