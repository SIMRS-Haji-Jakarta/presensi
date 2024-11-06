package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengaduan;
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

public interface PengaduanDao extends JpaRepository<Pengaduan, String> {
    @Query(value = "select p from Pengaduan p where p.pegawai=:pegawai and year(p.tanggal)=:tahun order by p.tanggal desc")
    Page<Pengaduan> findAllByPegawaiAndTahunOrderByTanggalDesc(@Param("pegawai") Pegawai pegawai, @Param("tahun") Integer tahun, Pageable page);

    Optional<Pengaduan> findByPegawaiAndId(Pegawai pegawai, String id);

    Page<Pengaduan> findAllByTanggalGreaterThanEqualAndTanggalLessThanEqualAndStatusVerifikasiOrderByTanggalDesc(LocalDateTime tanggal, LocalDateTime tanggal2, StatusVerifikasi statusVerifikasi, Pageable pageable);

    Page<Pengaduan> findAllByStatusVerifikasiOrderByTanggalDesc(StatusVerifikasi statusVerifikasi, Pageable page);

    @Query(value = "select p from Pengaduan p left join Pegawai p2 on (p2.id = p.pegawai.id) where p.tanggal between :tanggal and :tanggal2 and p2.unitKerjaPresensi.id in (:listUnitKerja) order by p.tanggal desc")
    Page<Pengaduan> findAllBySQLTanggalGreaterThanEqualAndTanggalLessThanEqualAndAdminUnitKerjaOrderByTanggalDesc(@Param("tanggal") LocalDateTime tanggal,
                                                                                                                  @Param("tanggal2") LocalDateTime tanggal2,
                                                                                                                  @Param("listUnitKerja") List<Long> listUnitKerja,
                                                                                                                  Pageable pageable);

    @Query(value = "select p from Pengaduan p left join Pegawai p2 on (p2.id = p.pegawai.id) where p2.unitKerjaPresensi.id in (:listUnitKerja) order by p.tanggal desc")
    Page<Pengaduan> findAllBySQLAdminUnitKerjaOrderByTanggalDesc(@Param("listUnitKerja") List<Long> listUnitKerja, Pageable page);

    List<Pengaduan> findAllByStatusVerifikasiAndTanggalBefore(StatusVerifikasi statusVerifikasi, LocalDateTime tanggal);

    Page<Pengaduan> findAll(Specification<Pengaduan> specificationBySearchParamAndPage, Pageable page);
}
