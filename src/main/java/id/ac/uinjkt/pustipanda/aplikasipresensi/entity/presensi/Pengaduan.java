package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(schema = "presensi", name = "pengaduan")
@Data
public class Pengaduan extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;

    @Column(name = "tanggal")
    private LocalDateTime tanggal = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "tanggal_mulai", columnDefinition = "DATE")
    private LocalDate tanggalMulai;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "tanggal_selesai", columnDefinition = "DATE")
    private LocalDate tanggalSelesai;

    @Size(min = 1, max = 250)
    @NotEmpty(message = "Deskripsi tidak boleh kosong")
    @Column(name = "deskripsi", length = 250)
    private String deskripsi;

    @ManyToOne
    @JoinColumn(name = "id_status_verifikasi")
    private StatusVerifikasi statusVerifikasi;

    @Column(name = "url_bukti", length = 250)
    private String urlBukti;

    @Column(name = "catatan", length = 250)
    private String catatan;

    public Pengaduan() {
    }

}
