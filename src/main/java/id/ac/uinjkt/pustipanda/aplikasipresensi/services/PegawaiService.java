package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
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
public class PegawaiService {

    @Autowired
    private PegawaiDao pegawaiDao;

    public boolean isNomorAbsenDuplicate(Pegawai pegawai) {
        List<Pegawai> pegawaiList = pegawaiDao.findAllByNomorAbsen(pegawai.getNomorAbsen());
        for (Pegawai p : pegawaiList) {
            if (p != pegawai)
                return true;
        }
        return false;
    }

    public Optional<Pegawai> findById(Long id) {
        return pegawaiDao.findById(id);
    }

    public List<Pegawai> findAllByStatusAktif() {
        return pegawaiDao.findAllByStatusAktif();
    }

    public Pegawai getPejabatKepalaBiroAUK() {
        Pegawai pejabat = pegawaiDao.getPegawaiKepalaBiroAUK();
        if (pejabat != null) {
            pejabat.setNama(Pegawai.toStringNama(pejabat));
            pejabat.getJabatanPegawai().setNama("Kepala Biro AUK");
        } else
            pejabat = new Pegawai();

        return pejabat;
    }

    public void setIsFace(Pegawai pegawai, boolean status) {
        pegawai.setIsFace(status);
        pegawaiDao.save(pegawai);
    }

    public void save(Pegawai data) {
        pegawaiDao.save(data);
    }

    public Page<Pegawai> getAllDataBySearchParam(String search, UnitKerjaPresensi unitKerjaPresensi, StatusPegawai statusPegawai, Pageable page) {
        return pegawaiDao.findAll(getSpecificationBySearchParam(search, unitKerjaPresensi, statusPegawai), page);
    }

    private Specification<Pegawai> getSpecificationBySearchParam(String name, UnitKerjaPresensi unitKerjaPresensi, StatusPegawai statusPegawai) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("nama")), JpaUtil.getContainsLikePattern(name)));
            }

            if (unitKerjaPresensi != null) {
                predicates.add(builder.equal(root.get("unitKerjaPresensi"), unitKerjaPresensi));
            }

            if (statusPegawai != null) {
                predicates.add(builder.equal(root.get("statusPegawai"), statusPegawai));
            }

            query.orderBy(builder.desc(root.get("id")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public List<String> getAllJenisKelamin() {
        List<String> datas = new LinkedList<>();
        datas.add("Laki-laki");
        datas.add("Perempuan");

        return datas;
    }

    public Boolean isDuplicate(String nip, String nik, String nomorAbsen, Long id) {
        List<Pegawai> counts = pegawaiDao.findAll(getSpecificationForCountBySearchParam(nip, nik, nomorAbsen, id));
        if (!counts.isEmpty())
            return true;

        return false;
    }

    private Specification<Pegawai> getSpecificationForCountBySearchParam(String nip, String nik, String nomorAbsen, Long id) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nip != null && !nip.isEmpty()) {
                predicates.add(builder.equal(root.get("nip"), nip));
            }

            if (nik != null && !nik.isEmpty()) {
                predicates.add(builder.equal(root.get("nik"), nik));
            }

            if (nomorAbsen != null && !nomorAbsen.isEmpty()) {
                predicates.add(builder.equal(root.get("nomorAbsen"), nomorAbsen));
            }

            List<Predicate> predicates2 = new ArrayList<>();
            if (id != null) {
                predicates2.add(builder.notEqual(root.get("id"), id));
            }

            query.orderBy(builder.asc(root.get("id")));

            return builder.and(builder.and(predicates2.toArray(new Predicate[0])),
                    builder.or(predicates.toArray(new Predicate[0])));
        });
    }

    public void delete(Pegawai pegawai) {
        pegawaiDao.delete(pegawai);
    }
}
