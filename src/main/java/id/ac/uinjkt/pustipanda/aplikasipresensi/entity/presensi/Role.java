package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "tbmrole")
@Data
public class Role implements Serializable {
    @Id
    @Column(name = "roleid", unique = true, length = 50)
    private String roleId;

    @Column(name = "rolename", length = 255)
    private String roleName;
}
