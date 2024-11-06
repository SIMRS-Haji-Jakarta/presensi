package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "referensi", name = "jenis_pegawai")
@Data
public class JenisPegawai implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "nama", length = 255)
    private String nama;

    @ManyToOne
    @JoinColumn(name = "kategori_pegawai")
    private KategoriPegawai kategoriPegawai;

}
