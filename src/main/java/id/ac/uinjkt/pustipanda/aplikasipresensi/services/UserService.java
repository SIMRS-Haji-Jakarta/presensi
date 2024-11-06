package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UserDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Role;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.User;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Page<User> getAllDataBySearchParam(String name, Role role, Pageable page) {
        return userDao.findAll(getSpecificationBySearchParam(name, role), page);
    }

    private Specification<User> getSpecificationBySearchParam(String name, Role role) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(builder.or(builder.like(builder.lower(root.get("userId")), JpaUtil.getContainsLikePattern(name)),
                        builder.like(builder.lower(root.get("pegawai").get("nama")), JpaUtil.getContainsLikePattern(name))));
            }

            if (role != null) {
                predicates.add(builder.equal(root.get("role"), role));
            }

            query.orderBy(builder.asc(root.get("userId")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });

    }

    public Optional<User> findByUserId(String id) {
        return userDao.findByUserId(id);
    }

    public Optional<User> findUserByUserIdNot(String userId, String userId1) {
        return userDao.findByUserIdAndUserIdNot(userId, userId1);
    }

    public void save(User user) {
        userDao.save(user);
    }

    public void delete(User user) {
        userDao.delete(user);
    }
}
