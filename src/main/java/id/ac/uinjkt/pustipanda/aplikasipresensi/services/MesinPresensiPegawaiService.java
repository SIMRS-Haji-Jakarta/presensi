package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.MesinPresensiPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeMesinPresensiPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
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
public class MesinPresensiPegawaiService {

    @Autowired
    private MesinPresensiPegawaiDao mesinPresensiPegawaiDao;

    @Autowired
    private PegawaiService pegawaiService;

    @Autowired
    private MesinPresensiService mesinPresensiService;

    public List<EmployeeMesinPresensiPegawaiDto> getPegawaisByMesinPresensiAndTipePegawai(MesinPresensi mesinPresensi, Integer tipePegawai) {
        if (tipePegawai.equals(AppConstant.PERSONNEL_POSITION_DOSEN))
            return mesinPresensiPegawaiDao.findAllPegawaiByMesinPresensiAndTipePegawaiDosen(mesinPresensi);
        else if (tipePegawai.equals(AppConstant.PERSONNEL_POSITION_TENDIK))
            return mesinPresensiPegawaiDao.findAllPegawaiByMesinPresensiAndTipePegawaiTendik(mesinPresensi);

        return new LinkedList<>();
    }

    public Page<MesinPresensiPegawai> getFilterBySearchParamAndPage(String nama, String namaMesin, UnitKerjaPresensi unitKerjaPresensi, Pageable page) {
        return mesinPresensiPegawaiDao.findAll(getSpecificationBySearchParam(nama, namaMesin, unitKerjaPresensi), page);
    }

    public Page<MesinPresensiPegawai> getFilterByNamaOrMesinPresensiByListMesinPresensiUnitAndPage(String nama, MesinPresensi mesinPresensi, List<MesinPresensiUnit> mesinPresensiUnitList, Pageable page) {
        List<MesinPresensi> mesinPresensis = mesinPresensiService.getAllByListMesinPresensiUnit(mesinPresensiUnitList);
        return mesinPresensiPegawaiDao.findAll(getSpecificationByNamaOrListMesinAdminUnit(nama, mesinPresensi, mesinPresensis), page);
    }

    private Specification<MesinPresensiPegawai> getSpecificationByNamaOrListMesinAdminUnit(String nama, MesinPresensi mesinPresensi, List<MesinPresensi> mesinPresensis) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nama != null && !nama.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), CommonUtils.getContainsLikePattern(nama)));
            }

            if (mesinPresensi != null) {
                predicates.add(builder.equal(root.get("mesinPresensi"), mesinPresensi));
            } else {
                predicates.add(root.get("mesinPresensi").in(mesinPresensis));
            }

            query.orderBy(builder.desc(root.get("updatedAt")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Specification<MesinPresensiPegawai> getSpecificationBySearchParam(String nama, String namaMesin, UnitKerjaPresensi unitKerjaPresensi) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nama != null && !nama.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("pegawai").get("nama")), CommonUtils.getContainsLikePattern(nama)));
            }

            if (namaMesin != null && !namaMesin.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("mesinPresensi").get("nama")), CommonUtils.getContainsLikePattern(namaMesin)));
            }

            if (unitKerjaPresensi != null) {
                predicates.add(builder.equal(root.get("pegawai").get("unitKerjaPresensi"), unitKerjaPresensi));
            }

            query.orderBy(builder.desc(root.get("updatedAt")));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public List<MesinPresensiPegawai> findAllByMesinPresensi(MesinPresensi mesinPresensi) {
        return mesinPresensiPegawaiDao.findAllByMesinPresensi(mesinPresensi);
    }

    public Optional<MesinPresensiPegawai> findByPegawaiNomorAbsenAndMesinPresensi(String noAbsen, MesinPresensi mesinPresensi) {
        return mesinPresensiPegawaiDao.findFirstByPegawaiNomorAbsenAndMesinPresensi(noAbsen, mesinPresensi);
    }

    public Optional<MesinPresensiPegawai> findById(Long id) {
        return mesinPresensiPegawaiDao.findById(id);
    }

    public void save(MesinPresensiPegawai mesinPresensiPegawai) {
        mesinPresensiPegawaiDao.save(mesinPresensiPegawai);
    }

    public void delete(MesinPresensiPegawai mesinPresensiPegawai) {
        mesinPresensiPegawaiDao.delete(mesinPresensiPegawai);
    }

    public boolean isDataAlreadyExist(MesinPresensiPegawai mesinPresensiPegawai) {
        int i = 0;
        if (mesinPresensiPegawai.getId() == null)
            i = mesinPresensiPegawaiDao.countAllByPegawaiIdAndMesinPresensiId(mesinPresensiPegawai.getPegawai().getId(),
                    mesinPresensiPegawai.getMesinPresensi().getId());
        else
            i = mesinPresensiPegawaiDao.countAllByPegawaiIdAndMesinPresensiIdAndIdNot(mesinPresensiPegawai.getPegawai().getId(),
                    mesinPresensiPegawai.getMesinPresensi().getId(), mesinPresensiPegawai.getId());

        return i > 0;
    }

    public List<String> getListErrorNoAbsen(Pegawai pegawai) {
        List<String> err = new LinkedList<>();
        if (pegawai.getNomorAbsen() == null || pegawai.getNomorAbsen().isEmpty()) {
            err.add("Nomor Absen untuk pegawai ini masih kosong!");
        }
        if (pegawai.getNomorAbsen() != null && !CurrentService.isNumeric(pegawai.getNomorAbsen())) {
            err.add("Nomor Absen untuk pegawai ini tidak berupa Angka!");
        }

        if (pegawai.getNomorAbsen() != null && pegawaiService.isNomorAbsenDuplicate(pegawai)) {
            err.add("Nomor Absen untuk pegawai ini duplikat dengan Nomor Absen pegawai lain");
        }

        return err;
    }

    public void copyToMesinPresensi(MesinPresensi mesinPresensiTujuan, List<EmployeeMesinPresensiPegawaiDto> employeeMesinPresensiPegawaiDtos) {
        for (EmployeeMesinPresensiPegawaiDto data : employeeMesinPresensiPegawaiDtos) {
            int i = mesinPresensiPegawaiDao.countAllByPegawaiIdAndMesinPresensiId(data.getId(),
                    mesinPresensiTujuan.getId());
            if (i == 0) {

                Optional<Pegawai> pegawai = pegawaiService.findById(data.getId());
                if (pegawai.isPresent()) {
                    MesinPresensiPegawai mesinPresensiPegawai = new MesinPresensiPegawai();
                    mesinPresensiPegawai.setPegawai(pegawai.get());
                    mesinPresensiPegawai.setMesinPresensi(mesinPresensiTujuan);

                    save(mesinPresensiPegawai);
                    log.info("copy pegawai {} ke mesin {}", pegawai.get().getId(), mesinPresensiTujuan.getNama());
                }
            }
        }
    }

    public List<MesinPresensiPegawai> findAllByMesinPresensiAndTipePegawai(MesinPresensi mesinPresensiTujuan, Integer tipePegawai) {
        if (tipePegawai.equals(AppConstant.PERSONNEL_POSITION_DOSEN))
            return mesinPresensiPegawaiDao.findAllByMesinPresensiAndTipePegawaiDosen(mesinPresensiTujuan);
        else if (tipePegawai.equals(AppConstant.PERSONNEL_POSITION_TENDIK))
            return mesinPresensiPegawaiDao.findAllByMesinPresensiAndTipePegawaiTendik(mesinPresensiTujuan);

        return new LinkedList<>();
    }
}
