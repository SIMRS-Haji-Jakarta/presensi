package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "file_profile")
@Data
public class FileProfile {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_foto_profile")
    private FotoProfile fotoProfile;

    @Column(name = "nama", length = 250)
    private String nama;

    @Column(name = "type", length = 100)
    private String type;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;
}
