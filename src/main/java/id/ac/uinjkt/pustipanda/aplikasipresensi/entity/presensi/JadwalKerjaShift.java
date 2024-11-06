package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "presensi", name = "jadwal_kerja_shift")
@Data
public class JadwalKerjaShift extends BaseEntity{
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tanggal", columnDefinition = "DATE")
    private LocalDate tanggal;

    @ManyToOne
    @JoinColumn(name = "id_jam_kerja_shift")
    private JamKerjaShift jamKerjaShift;

    public JadwalKerjaShift() {
    }
}
