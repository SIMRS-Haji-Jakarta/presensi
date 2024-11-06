package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "presensi", name = "unit_kerja_presensi")
@Data
public class UnitKerjaPresensi implements Serializable {
    public final static Long UNIT_KERJA_PRESENSI_RS_HAJI = 563L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "nama", length = 255)
    private String nama;

    @ManyToOne
    @JoinColumn(name = "parent", nullable = true)
    private UnitKerjaPresensi parent;

    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit unit;
}
