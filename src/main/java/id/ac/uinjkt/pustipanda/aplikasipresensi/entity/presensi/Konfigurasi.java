package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "public", name = "konfigurasi")
@Data
public class Konfigurasi implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, unique = true, nullable = false)
    private Long id;

    @Column(name = "nama", length = 255)
    private String nama;

    @Column(name = "nilai")
    private String nilai;

    @Column(name = "keterangan", nullable = true)
    private String keterangan;
}
