package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(schema = "presensi", name = "pegawai_presensi_online")
@Data
public class PegawaiPresensiOnline extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "tanggal")
    private LocalDateTime tanggal = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;

    @JoinColumn(name = "oleh")
    private String oleh; //pegawai yang melakukan input

    public PegawaiPresensiOnline() {
    }
}
