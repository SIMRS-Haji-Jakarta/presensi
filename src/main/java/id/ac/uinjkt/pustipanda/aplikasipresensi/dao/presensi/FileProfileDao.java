package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FileProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileProfileDao extends JpaRepository<FileProfile, String> {

    Optional<FileProfile> findByFotoProfile(FotoProfile fotoProfile);
}
