package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Konfigurasi;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface KonfigurasiDao extends PagingAndSortingRepository<Konfigurasi, Long> {

    List<Konfigurasi> findByNamaAndNilai(String nama, String nilai);

    Optional<Konfigurasi> findFirstByNamaOrderByIdDesc(String nama);

    Optional<Konfigurasi> findFirstByNamaAndNilaiOrderByIdDesc(String nama, String nilai);
}
