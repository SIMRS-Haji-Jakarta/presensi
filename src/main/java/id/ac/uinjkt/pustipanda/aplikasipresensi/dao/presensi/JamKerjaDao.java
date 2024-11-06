package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JamKerja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JamKerjaDao extends PagingAndSortingRepository<JamKerja, String> {
    Page<JamKerja> findByNamaContainingIgnoreCase(String nama, Pageable pageable);

}
