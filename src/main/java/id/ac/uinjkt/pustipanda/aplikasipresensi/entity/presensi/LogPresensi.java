package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "presensi", name = "log_presensi")
@Data
public class LogPresensi extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "no_absen", length = 20)
    private String noAbsen;

    @Column(name = "waktu")
    private LocalDateTime waktu;

    @Column(name = "status", length = 5)
    private String status;

    @Column(name = "verify")
    private Integer verify;

    @Column(name = "sn", length = 50)
    private String sn;

    @Column(name = "op", length = 20)
    private String op;

    @Column(name = "stamp", length = 20)
    private String stamp;

    @Column(name = "is_sync")
    private Boolean isSync = false;
}
