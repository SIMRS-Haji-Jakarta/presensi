package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "presensi", name = "absensi")
@Data
public class Absensi extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;

    @Column(name = "tanggal", columnDefinition = "DATE")
    private LocalDate tanggal;

    @ManyToOne
    @JoinColumn(name = "id_status_presensi")
    private StatusPresensi statusPresensi;

    @Column(name = "waktu_in", columnDefinition = "TIME")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime waktuIn;

    @Column(name = "waktu_in_ip", length = 64)
    private String waktuInIp;

    @Column(name = "waktu_in_lat")
    private Double waktuInLat;

    @Column(name = "waktu_in_lgn")
    private Double waktuInLgn;

    @Column(name = "tanggal_out", columnDefinition = "DATE")
    private LocalDate tanggalOut; // pegawai biasa samakan dgn tanggal
                                  // utk pegawai shift sesuaikan dgn tgl out, setelah di periksa di jadwal_kerja_shift

    @Column(name = "waktu_out", columnDefinition = "TIME")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime waktuOut;

    @Column(name = "waktu_out_ip", length = 64)
    private String waktuOutIp;

    @Column(name = "waktu_out_lat")
    private Double waktuOutLat;

    @Column(name = "waktu_out_lgn")
    private Double waktuOutLgn;

    public Absensi() {
    }

    public Absensi(Pegawai pegawai, LocalDate tanggal) {
        this.pegawai = pegawai;
        this.tanggal = tanggal;
    }
}
