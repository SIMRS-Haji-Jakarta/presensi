package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FilePengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePengajuanDao extends JpaRepository<FilePengajuan, String> {

    Optional<FilePengajuan> findByPengajuan(Pengajuan pengajuan);
}
