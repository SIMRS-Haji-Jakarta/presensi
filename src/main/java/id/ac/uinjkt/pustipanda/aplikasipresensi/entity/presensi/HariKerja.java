package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "presensi", name = "hari_kerja")
@Data
public class HariKerja {
    @Id
    @Column(name = "tanggal", columnDefinition = "DATE")
    private LocalDate tanggal;

    @ManyToOne
    @JoinColumn(name = "id_status_hari")
    private StatusHari statusHari;

    @ManyToOne
    @JoinColumn(name = "id_jam_kerja")
    private JamKerja jamKerja;
}
