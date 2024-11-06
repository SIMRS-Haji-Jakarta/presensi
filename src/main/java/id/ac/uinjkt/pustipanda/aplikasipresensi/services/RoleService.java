package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.RoleDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    public List<Role> findAllRole() {
        return roleDao.findAll();
    }
}
