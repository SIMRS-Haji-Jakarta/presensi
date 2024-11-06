package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "presensi", name = "mesin_presensi")
@Data
public class MesinPresensi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "nama", length = 255)
    private String nama;

    @Column(name = "lokasi", length = 200)
    private String lokasi;

    // untuk mapping area di wdms
    @Column(name = "kode_area")
    private Integer kodeArea;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "sn", length = 50)
    private String sn;

}
