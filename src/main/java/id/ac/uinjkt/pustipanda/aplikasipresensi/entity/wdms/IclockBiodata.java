package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iclock_biodata", schema = "public")
@Data
public class IclockBiodata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "change_time")
    private LocalDateTime changeTime;

    @Column(name = "change_user")
    private String changeUser;

    @Column(name = "status")
    private short status;

    @Column(name = "bio_tmp")
    private String bioTmp;

    @Column(name = "bio_no")
    private Integer bioNo;

    @Column(name = "bio_index")
    private Integer bioIndex;

    @Column(name = "bio_type")
    private int bioType;

    @Column(name = "major_ver")
    private String majorVer;

    @Column(name = "minor_ver")
    private String minorVer;

    @Column(name = "bio_format")
    private Integer bioFormat;

    @Column(name = "valid")
    private int valid;

    @Column(name = "duress")
    private int duress;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "sn")
    private String sn;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private PersonnelEmployee employeeId;

}
