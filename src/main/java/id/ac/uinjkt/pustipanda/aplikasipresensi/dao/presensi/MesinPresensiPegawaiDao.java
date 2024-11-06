package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeMesinPresensiPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MesinPresensiPegawaiDao extends JpaRepository<MesinPresensiPegawai, Long>, JpaSpecificationExecutor<MesinPresensiPegawai> {

    int countAllByPegawaiIdAndMesinPresensiId(Long idPegawai, Long idMesin);

    int countAllByPegawaiIdAndMesinPresensiIdAndIdNot(Long idPegawai, Long idMesin, Long id);

    Optional<MesinPresensiPegawai> findFirstByPegawaiNomorAbsenAndMesinPresensi(String noAbsen, MesinPresensi mesinPresensi);

    List<MesinPresensiPegawai> findAllByMesinPresensi(MesinPresensi mesinPresensi);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeMesinPresensiPegawaiDto(p.id) from MesinPresensiPegawai mpp left join " +
            "Pegawai p on (p = mpp.pegawai) where mpp.pegawai is not null and mpp.mesinPresensi=:mesinPresensi and p.jenisPegawai.id in (1,3,7)")
    List<EmployeeMesinPresensiPegawaiDto> findAllPegawaiByMesinPresensiAndTipePegawaiDosen(@Param("mesinPresensi") MesinPresensi mesinPresensi);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeMesinPresensiPegawaiDto(p.id) from MesinPresensiPegawai mpp left join " +
            "Pegawai p on (p = mpp.pegawai) where mpp.pegawai is not null and mpp.mesinPresensi=:mesinPresensi and p.jenisPegawai.id not in (1,3,7)")
    List<EmployeeMesinPresensiPegawaiDto> findAllPegawaiByMesinPresensiAndTipePegawaiTendik(@Param("mesinPresensi") MesinPresensi mesinPresensi);

    @Query("select mpp from MesinPresensiPegawai mpp left join " +
            "Pegawai p on (p = mpp.pegawai) where mpp.pegawai is not null and mpp.mesinPresensi=:mesinPresensi and p.jenisPegawai.id in (1,3,7)")
    List<MesinPresensiPegawai> findAllByMesinPresensiAndTipePegawaiDosen(@Param("mesinPresensi") MesinPresensi mesinPresensi);

    @Query("select mpp from MesinPresensiPegawai mpp left join " +
            "Pegawai p on (p = mpp.pegawai) where mpp.pegawai is not null and mpp.mesinPresensi=:mesinPresensi and p.jenisPegawai.id not in (1,3,7)")
    List<MesinPresensiPegawai> findAllByMesinPresensiAndTipePegawaiTendik(@Param("mesinPresensi") MesinPresensi mesinPresensi);
}
