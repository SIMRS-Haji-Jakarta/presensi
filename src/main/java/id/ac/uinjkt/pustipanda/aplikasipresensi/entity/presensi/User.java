package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "tbmuser")
@Data
public class User implements Serializable {
    @Id
    @Column(name = "userid", unique = true, length = 255)
    private String userId;

    @Column(name = "userpassword",  length = 255)
    private String userPassword;

    @ManyToOne
    @JoinColumn(name = "userrole")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "pegawai")
    private Pegawai pegawai;

    @Column(name = "aktif")
    private Boolean aktif;
}
