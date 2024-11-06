package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PegawaiDao extends JpaRepository<Pegawai, Long> {

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' order by a.nama")
    List<EmployeeDto> findAllEmployee();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' and a.unitKerjaPresensi is not null order by a.unitKerjaPresensi.id asc, a.nama asc")
    List<EmployeeDto> findAllEmployeeSortByUnitKerja();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' and a.jenisPegawai.id in (1,3,7) order by a.nama")
    List<EmployeeDto> findAllEmployeeDosen();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi is not null order by a.unitKerjaPresensi.id asc, a.nama asc")
    List<EmployeeDto> findAllEmployeeDosenSortByUnitKerja();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' order by a.nama")
    List<EmployeeDto> findAllEmployeeShift();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' and b.adminUnit.id = :unit order by a.nama")
    List<EmployeeDto> findAllEmployeeShiftByUnit(@Param("unit") String unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' and b.adminUnit.id = :unit and lower(a.nama) like %:name% order by a.nama")
    List<EmployeeDto> findAllEmployeeShiftByUnitByName(@Param("unit") String unit, @Param("name") String name);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeByName(@Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' and a.jenisPegawai.id in (1,3,7) and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeDosenByName(@Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeShiftByName(@Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, AdminUnit au where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi member of au.unitKerja  and au.id=:unit order by a.nama")
    List<EmployeeDto> findAllEmployeeByUnit(@Param("unit") String unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, AdminUnit au where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi member of au.unitKerja  and au.id=:unit and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeByUnitByName(@Param("unit") String unit, @Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit = u  and u.id =:unit order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByUnit(@Param("unit") Long unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit = u and u.id=:unit and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByUnitByName(@Param("unit") Long unit, @Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.unit = u and u.id =:unit order by a.nama")
    List<EmployeeDto> findAllEmployeeDosenAdminPusatByUnit(@Param("unit") Long unit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.unit = u and u.id=:unit and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeDosenAdminPusatByUnitByName(@Param("unit") Long unit, @Param("nama") String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) order by a.nama")
    List<EmployeeDto> findAllEmployeeByListUnitKerja(@Param("listIdUnitKerja") List<Long> listIdUnitKerja);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.id in (:listIdUnitKerja) order by a.nama")
    List<EmployeeDto> findAllEmployeeDosenByListUnitKerja(@Param("listIdUnitKerja") List<Long> listIdUnitKerja);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeByListUnitKerjaByName(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("nama") String nama);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnit(@Param("listIdUnitKerja") List<Long> listIdUnitKerja);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnitByPegawaiShift(@Param("listIdUnitKerja") List<Long> listIdUnitKerja);

    @Query(value = "select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.id in (:listIdUnitKerja) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByListUnit(@Param("listIdUnitKerja") List<Long> listIdUnitKerja);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) and a.jenisPegawai.id = :idJenisPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawai(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("idJenisPegawai") Long idJenisPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) and a.jenisPegawai.id = :idJenisPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnitKerjaAndJenisPegawaiByPegawaiShift(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("idJenisPegawai") Long idJenisPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawai(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.id in (:listIdUnitKerja) and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByListUnitKerjaAndKategoriPegawaiByPegawaiShift(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.id in (:listIdUnitKerja) and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByListUnitKerjaAndKategoriPegawai(@Param("listIdUnitKerja") List<Long> listIdUnitKerja, @Param("idKategoriPegawai") Long idKategoriPegawai);

    Pegawai findFirstByUnitKerjaPresensiIdOrderById(Long idUnitKerja);

    @Query(value = "SELECT r.pegawai FROM employ.riwayat_jabatan r where r.jabatan_tambahan = 71 and r.status = true and r.aktif_jabatan_tambahan = true", nativeQuery = true)
    Long findIdRektor();

    Pegawai findAllByJenisPegawaiKategoriPegawaiIdAndId(Long idKategoriPegawai, Long idPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByKategoriPegawai(@Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByKategoriPegawaiShift(@Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByKategoriPegawai(@Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit.id = :idUnit order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByUnit(@Param("idUnit") Long idUnit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a=ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit.id = :idUnit order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByUnitByPegawaiShift(@Param("idUnit") Long idUnit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.unit.id = :idUnit order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByUnit(@Param("idUnit") Long idUnit);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit.id = :idUnit and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByUnitAndKategoriPegawai(@Param("idUnit") Long idUnit, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit.id = :idUnit and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByUnitAndKategoriPegawaiByPegawaiShift(@Param("idUnit") Long idUnit, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminPusatByPegawai(@Param("idPegawai") Long idPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminPusatByPegawaiShift(@Param("idPegawai") Long idPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminPusatByPegawaiAndKategoriPegawai(@Param("idPegawai") Long idPegawai, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminPusatByPegawaiAndKategoriPegawaiShift(@Param("idPegawai") Long idPegawai, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai and a.jenisPegawai.id in (1,3,7) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminPusatByPegawai(@Param("idPegawai") Long idPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.id = :idPegawai and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and a.jenisPegawai.id in (1,3,7) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminPusatByPegawaiAndKategoriPegawai(@Param("idPegawai") Long idPegawai, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) and a.unitKerjaPresensi.unit.id = :idUnit and a.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByUnitAndKategoriPegawai(@Param("idUnit") Long idUnit, @Param("idKategoriPegawai") Long idKategoriPegawai);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByAll();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a join PegawaiShift ps on (a = ps.pegawai) " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeAdminUnitByAllByPegawaiShift();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeAdminUnitDto(a.id, CASE WHEN (a.nip is null ) THEN '' ELSE a.nip END, a.nama, g.nama, j.nama) from Pegawai a " +
            "left join Golongan g on (g = a.golongan) left join JabatanPegawai j on (j = a.jabatanPegawai) where a.statusPegawai='1' " +
            " and a.jenisPegawai.id in (1,3,7) order by a.nama")
    List<EmployeeAdminUnitDto> findAllEmployeeDosenAdminUnitByAll();

    @Query("select p from Pegawai p where p.statusPegawai='1'")
    List<Pegawai> findAllByStatusAktif();

    @Query(value = "select p from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p = :pegawai and p.statusPegawai.id = 1L order by p.nama")
    List<Pegawai> findAllPegawaiBelumUploadFotoByPegawaiOrderByName(Pegawai pegawai, Pageable page);

    @Query(value = "select count(p.id) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p = :pegawai and p.statusPegawai.id = 1L")
    Integer countAllPegawaiBelumUploadFotoByPegawaiOrderByName(Pegawai pegawai);

    @Query(value = "select p from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.id = :idPegawai and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.statusPegawai.id = 1L order by p.nama")
    List<Pegawai> findAllPegawaiBelumUploadFotoByKategoriPegawaiIdAndPegawaiIdOrderByName(Long idKategoriPegawai, Long idPegawai, Pageable page);

    @Query(value = "select count(p.id) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.id = :idPegawai and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.statusPegawai.id = 1L")
    Integer countAllPegawaiBelumUploadFotoByKategoriPegawaiIdAndPegawaiIdOrderByName(Long idKategoriPegawai, Long idPegawai);

    @Query(value = "select p from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.statusPegawai.id = 1L order by p.nama")
    List<Pegawai> findAllPegawaiBelumUploadFotoByKategoriPegawaiIdOrderByName(Long idKategoriPegawai, Pageable page);

    @Query(value = "select count(p.id) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.jenisPegawai.kategoriPegawai.id = :idKategoriPegawai and p.statusPegawai.id = 1L")
    Integer countAllPegawaiBelumUploadFotoByKategoriPegawaiIdOrderByName(Long idKategoriPegawai);

    @Query(value = "select p from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.id is not null and p.statusPegawai.id = 1L order by p.nama")
    List<Pegawai> findAllPegawaiBelumUploadFotoOrderByName(Pageable page);

    @Query(value = "select count(p.id) from Pegawai p left join FotoProfile a on (p = a.pegawai)" +
            " where a.id is null and p.id is not null and p.statusPegawai.id = 1L")
    Integer countAllPegawaiBelumUploadFotoOrderByName();

    List<Pegawai> findAllByNomorAbsen(String noAbsen);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit = u and u.id=:unit and a.jenisPegawai.kategoriPegawai.id = :kategori order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByUnitByKategori(Long unit, Long kategori);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a, Unit u where a.statusPegawai='1' " +
            " and a.unitKerjaPresensi.unit = u and u.id=:unit and a.jenisPegawai.kategoriPegawai.id = :kategori and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByUnitByKategoriByName(Long unit, Long kategori, String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            " and a.jenisPegawai.kategoriPegawai.id = :kategori order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByKategori(Long kategori);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            " and a.jenisPegawai.kategoriPegawai.id = :kategori and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeAdminPusatByKategoriByName(Long kategori, String nama);

    @Query(value = "SELECT x.* FROM public.pegawai x " +
            "WHERE x.jabatan_fungsional = 51 and x.unit_kerja=331 and x.status_kepegawaian =1 order by x.tmt_jabatan_utama desc limit 1", nativeQuery = true)
    Pegawai getPegawaiKepalaBiroAUK();

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            "and a.unitKerjaPresensi.id = :idUnitKerja order by a.nama")
    List<EmployeeDto> findAllEmployeeByUnitKerjaPresensi(Long idUnitKerja);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from Pegawai a where a.statusPegawai='1' " +
            "and a.unitKerjaPresensi.id = :idUnitKerja and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeByUnitKerjaPresensiAndName(Long idUnitKerja, String nama);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' " +
            "and a.unitKerjaPresensi.id = :idUnitKerja order by a.nama")
    List<EmployeeDto> findAllEmployeeShiftByUnitKerjaPresensi(Long idUnitKerja);

    @Query("select new id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto(a.id, a.nomorAbsen, a.gelarDepan, a.nama, a.gelarBelakang) from PegawaiShift b join Pegawai a on b.pegawai = a where a.statusPegawai='1' " +
            "and a.unitKerjaPresensi.id = :idUnitKerja and lower(a.nama) like %:nama% order by a.nama")
    List<EmployeeDto> findAllEmployeeShiftByUnitKerjaPresensiAndNama(Long idUnitKerja, String nama);

    Page<Pegawai> findAll(Specification<Pegawai> specificationBySearchParam, Pageable page);

    List<Pegawai> findAll(Specification<Pegawai> specificationBySearchParam);

    Optional<Pegawai> findByNipOrNikOrNomorAbsenAndIdNot(String nip, String nik, String nomorAbsen, Long id);

    Optional<Pegawai> findByNipOrNikOrNomorAbsen(String nip, String nik, String nomorAbsen);

    Integer findByNipAndIdNot(String nip, Long id);

    Integer findByNikAndIdNot(String nip, Long id);

    Integer findByNomorAbsenAndIdNot(String nip, Long id);

    Long count(Specification<Pegawai> specificationForCountBySearchParam);
}
