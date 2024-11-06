package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "status_verifikasi")
@Data
public class StatusVerifikasi {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "kode", length = 5)
    private String kode;

    @Column(name = "nama", length = 20)
    private String nama;

    public StatusVerifikasi() {
    }

    public StatusVerifikasi(String id) {
        this.id = id;
    }
}
