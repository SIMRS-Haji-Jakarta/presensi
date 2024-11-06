package id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserDao extends PagingAndSortingRepository<User, String> {
    User findByAktifTrueAndUserId(String userId);

    Page<User> findAll(Specification<User> specificationBySearchParam, Pageable page);

    Optional<User> findByUserId(String id);

    Optional<User> findByUserIdAndUserIdNot(String userId, String userId1);
}
