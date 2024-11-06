package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;

@Entity
@Table(schema = "presensi", name = "jam_kerja_shift")
@Data
public class JamKerjaShift {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @Size(min = 1, max = 3)
    @NotEmpty(message = "Kode jam kerja shift tidak boleh kosong")
    @Column(name = "kode", length = 3)
    private String kode;

    @Size(min = 1, max = 50)
    @NotEmpty(message = "Nama jam kerja tidak boleh kosong")
    @Column(name = "nama", length = 50)
    private String nama;

    @NotNull(message = "Waktu datang jam kerja tidak boleh kosong")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "datang", columnDefinition = "TIME")
    private LocalTime datang;

    @NotNull(message = "Waktu pulang jam kerja tidak boleh kosong")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "pulang", columnDefinition = "TIME")
    private LocalTime pulang;

    public JamKerjaShift() {
    }

    public JamKerjaShift(String id) {
        this.id = id;
    }
}