package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FotoProfileDao extends PagingAndSortingRepository<FotoProfile, String> {

    Optional<FotoProfile> findByPegawai(Pegawai pegawai);

    Page<FotoProfile> findAllByOrderByCreatedAtDesc(Pageable page);

    Page<FotoProfile> findAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdOrderByCreatedAtDesc(Long idKategoriPegawai, Long idPegawai, Pageable page);

    Page<FotoProfile> findAllByPegawaiJenisPegawaiKategoriPegawaiIdOrderByCreatedAtDesc(Long idKategoriPegawai, Pageable page);

    Page<FotoProfile> findAllByPegawaiOrderByCreatedAtDesc(Pegawai pegawai, Pageable page);

    List<FotoProfile> findAllByStatusOrderByCreatedAtDesc(Boolean status, Pageable page);

    Integer countAllByStatusOrderByCreatedAtDesc(Boolean status);

    List<FotoProfile> findAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Long idPegawai, Boolean status, Pageable page);

    Integer countAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Long idPegawai, Boolean status);

    List<FotoProfile> findAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Boolean status, Pageable page);

    Integer countAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Boolean status);

    List<FotoProfile> findAllByPegawaiAndStatusOrderByCreatedAtDesc(Pegawai pegawai, Boolean status, Pageable page);

    Integer countAllByPegawaiAndStatusOrderByCreatedAtDesc(Pegawai pegawai, Boolean status);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from FotoProfile a left join Pegawai p on (p = a.pegawai)" +
            " where a.status = :status and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by p.nama")
    List<FotoProfileDto> fotoProfilePegawaiByStatusAndKategoriPegawai(@Param("idKategoriPegawai") Long idKategoriPegawai, @Param("status") Boolean status);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from FotoProfile a left join Pegawai p on (p = a.pegawai)" +
            " where a.status = :status order by p.nama")
    List<FotoProfileDto> fotoProfilePegawaiByStatus(@Param("status") Boolean status);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from FotoProfile a left join Pegawai p on (p = a.pegawai)" +
            " where a.status = :status and p.unitKerjaPresensi.unit.id = :unit order by p.nama")
    List<FotoProfileDto> fotoProfilePegawaiByStatusAndUnit(@Param("status") Boolean status, @Param("unit") Long unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from FotoProfile a left join Pegawai p on (p = a.pegawai)" +
            " where a.status = :status and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.unitKerjaPresensi.unit.id = :unit order by p.nama")
    List<FotoProfileDto> fotoProfilePegawaiByStatusAndKategoriPegawaiAndUnit(@Param("idKategoriPegawai") Long idKategoriPegawai, @Param("unit") Long unit, @Param("status") Boolean status);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.statusPegawai.id = 1L order by p.nama")
    List<FotoProfileDto> fotoProfileBelumUploadByKategoriPegawai(@Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.unitKerjaPresensi.unit.id = :unit and p.statusPegawai.id = 1L order by p.nama")
    List<FotoProfileDto> fotoProfileBelumUploadByUnit(@Param("unit") Long unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.unitKerjaPresensi.unit.id = :unit and p.statusPegawai.id = 1L order by p.nama")
    List<FotoProfileDto> fotoProfileBelumUploadByKategoriPegawaiAndUnit(@Param("idKategoriPegawai") Long idKategoriPegawai, @Param("unit") Long unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto(a.id, p.id, CASE WHEN (p.nik is null ) THEN '' ELSE p.nik END, CASE WHEN (p.nip is null ) THEN '' ELSE p.nip END, p.nama, p.jenisKelamin, p.jenisPegawai.kategoriPegawai.nama, p.unitKerjaPresensi.nama, p.unitKerjaPresensi.unit.nama, a.urlBukti, a.status) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.statusPegawai.id = 1L order by p.nama")
    List<FotoProfileDto> fotoProfileBelumUpload();
}
