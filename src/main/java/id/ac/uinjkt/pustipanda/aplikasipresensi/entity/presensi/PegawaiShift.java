package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "pegawai_shift")
@Data
public class PegawaiShift extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_admin_unit")
    private AdminUnit adminUnit;

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;

    public PegawaiShift() {
    }

}
