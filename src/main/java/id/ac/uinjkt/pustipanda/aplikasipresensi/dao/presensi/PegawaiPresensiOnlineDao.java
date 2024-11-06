package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.PegawaiPresensiOnline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PegawaiPresensiOnlineDao extends JpaRepository<PegawaiPresensiOnline, String> {
    Page<PegawaiPresensiOnline> findAll(Specification<PegawaiPresensiOnline> specificationBySearchParamAndPage, Pageable page);

    int countAllByPegawai(Pegawai pegawai);
}
