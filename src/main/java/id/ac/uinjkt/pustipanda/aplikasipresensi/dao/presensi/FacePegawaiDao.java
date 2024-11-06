package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FacePegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FacePegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacePegawaiDao extends JpaRepository<FacePegawai, String>, JpaSpecificationExecutor<FacePegawai> {
    FacePegawai findByPegawaiOrderByPegawaiIdAsc(Pegawai pegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FacePegawaiDto(a.id, p.gelarDepan, p.nama, p.gelarBelakang) from FacePegawai a left join " +
            "MesinPresensiPegawai mpp on (mpp.pegawai = a.pegawai) left join Pegawai p on (p = a.pegawai) where a.pegawai is not null and mpp.mesinPresensi=:mesinPresensi order by a.id")
    List<FacePegawaiDto> findAllFacePegawaiDtoByMesinPresensi(@Param("mesinPresensi") MesinPresensi mesinPresensi);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FacePegawaiDto(a.id, p.gelarDepan, p.nama, p.gelarBelakang) from FacePegawai a left join " +
            "MesinPresensiPegawai mpp on (mpp.pegawai = a.pegawai) left join Pegawai p on (p = a.pegawai) where a.pegawai is not null and mpp.mesinPresensi=:mesinPresensi and lower(a.pegawai.nama) like %:name% order by a.id")
    List<FacePegawaiDto> findAllFacePegawaiDtoByMesinPresensiAndName(@Param("mesinPresensi") MesinPresensi mesinPresensi, @Param("name") String name);

    @Query("select a from FacePegawai a left join " +
            "MesinPresensiPegawai mpp on (mpp.pegawai = a.pegawai) left join Pegawai p on (p = a.pegawai) where a.pegawai is not null and mpp.mesinPresensi=:mesinPresensi order by a.id")
    List<FacePegawai> findAllFacePegawaiByMesinPresensi(MesinPresensi mesinPresensi);
}
