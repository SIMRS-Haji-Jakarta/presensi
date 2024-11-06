package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "area")
@Data
public class Area {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "nama")
    private String nama;

    @Column(name = "koordinate", columnDefinition = "TEXT")
    private String koordinat;
}
