package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "mesin_presensi_pegawai")
@Data
public class MesinPresensiPegawai extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mesin_presensi")
    private MesinPresensi mesinPresensi;

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;
}
