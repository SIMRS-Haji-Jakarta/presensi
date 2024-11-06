package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.LogPresensi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogPresensiDao extends JpaRepository<LogPresensi, Long> {
}
