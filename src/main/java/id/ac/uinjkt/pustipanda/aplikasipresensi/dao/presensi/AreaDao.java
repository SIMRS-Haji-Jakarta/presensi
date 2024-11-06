package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AreaDao extends PagingAndSortingRepository<Area, String> {
    Page<Area> findByNamaContainingIgnoreCaseOrderByIdAsc(String key, Pageable page);

    Page<Area> findAllByOrderByIdAsc(Pageable page);

    List<Area> findAllByOrderByIdAsc();
}
