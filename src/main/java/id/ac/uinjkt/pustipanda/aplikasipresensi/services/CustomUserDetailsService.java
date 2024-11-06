package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UserDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.User;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByAktifTrueAndUserId(username);
        //log.info("user : {}", user);

        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getUserPassword(), getGrantedAuthorities(user));
    }

    private Set<GrantedAuthority> getGrantedAuthorities(User user) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getRoleId()));

        AdminUnit adminUnit = adminUnitDao.findByPegawai(user.getPegawai());
        if (adminUnit != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority("aunit"));
        }
        log.info("authority: {} ", Arrays.toString(grantedAuthorities.stream().toArray()));

        return grantedAuthorities;
    }
}
