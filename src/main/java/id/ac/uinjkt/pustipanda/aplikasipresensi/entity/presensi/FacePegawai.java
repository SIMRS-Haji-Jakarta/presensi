package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(schema = "presensi", name = "face_pegawai")
@Data
public class FacePegawai extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai;

    @Column(name = "biodata", columnDefinition = "TEXT")
    private String biodata;

    @Column(name = "bio_no")
    private Integer bioNo;

    @Column(name = "bio_index")
    private Integer bioIndex;

    @Column(name = "bio_type")
    private Integer bioType;

    @Column(name = "minor_ver", length = 30)
    private String minorVer;

    @Column(name = "major_ver", length = 30)
    private String majorVer;

    @Column(name = "valid")
    private Integer valid;

    @Column(name = "duress")
    private Integer duress;

}
