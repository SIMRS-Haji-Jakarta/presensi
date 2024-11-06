package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusVerifikasi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PengajuanDao extends JpaRepository<Pengajuan, String> {
    @Query(value = "select p from Pengajuan p where p.pegawai=:pegawai and year(p.tanggal)=:tahun order by p.tanggal desc")
    Page<Pengajuan> findAllByPegawaiAndTahunOrderByTanggalDesc(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, Pageable page);

    Optional<Pengajuan> findByPegawaiAndId(Pegawai pegawai, String id);

    @Query(value = "select p from Pengajuan p left join Pegawai p2 on (p2.id = p.pegawai.id) where p.tanggal between :tanggal and :tanggal2 and p2.unitKerjaPresensi.id in (:listUnitKerja) order by p.tanggal desc")
    Page<Pengajuan> findAllBySQLTanggalGreaterThanEqualAndTanggalLessThanEqualAndAdminUnitKerjaOrderByTanggalDesc(@Param("tanggal") LocalDateTime tanggal,
                                                                                                                  @Param("tanggal2") LocalDateTime tanggal2,
                                                                                                                  @Param("listUnitKerja") List<Long> listUnitKerja,
                                                                                                                  Pageable pageable);

    @Query(value = "select p from Pengajuan p left join Pegawai p2 on (p2.id = p.pegawai.id) where p2.unitKerjaPresensi.id in (:listUnitKerja) order by p.tanggal desc")
    Page<Pengajuan> findAllBySQLAdminUnitKerjaOrderByTanggalDesc(@Param("listUnitKerja") List<Long> listUnitKerja, Pageable page);

    Page<Pengajuan> findAllByOrderByTanggalDesc(Pageable page);

    Page<Pengajuan> findAllByStatusVerifikasiOrderByTanggalDesc(StatusVerifikasi status, Pageable page);

    Page<Pengajuan> findAllByTanggalGreaterThanAndTanggalLessThanOrderByTanggalDesc(LocalDateTime tglMulai, LocalDateTime tglSelesai, Pageable page);

    Page<Pengajuan> findAllByTanggalGreaterThanAndTanggalLessThanAndStatusVerifikasiOrderByTanggalDesc(LocalDateTime tglMulai, LocalDateTime tglSelesai, StatusVerifikasi status, Pageable page);

    Page<Pengajuan> findAllByTanggalGreaterThanAndTanggalLessThanAndPegawaiOrderByTanggalDesc(LocalDateTime tglMulai, LocalDateTime tglSelesai, Pegawai pegawai, Pageable page);

    Page<Pengajuan> findAllByTanggalGreaterThanAndTanggalLessThanAndPegawaiAndStatusVerifikasiOrderByTanggalDesc(LocalDateTime tglMulai, LocalDateTime tglSelesai, Pegawai pegawai, StatusVerifikasi status, Pageable page);

    Page<Pengajuan> findAllByPegawaiOrderByTanggalDesc(Pegawai pegawai, Pageable page);

    Page<Pengajuan> findAllByPegawaiAndStatusVerifikasiOrderByTanggalDesc(Pegawai pegawai, StatusVerifikasi status, Pageable page);

    List<Pengajuan> findAllByStatusVerifikasiAndStatusProsesFalseOrderByTanggalDesc(StatusVerifikasi status);

    List<Pengajuan> findAllByStatusVerifikasiAndStatusProsesFalseAndTanggalBefore(StatusVerifikasi statusVerifikasi, LocalDateTime tanggal);

    Page<Pengajuan> findAll(Specification<Pengajuan> specificationBySearchParamAndPage, Pageable page);
}
