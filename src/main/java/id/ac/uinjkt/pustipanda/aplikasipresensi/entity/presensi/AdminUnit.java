package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "presensi", name = "admin_unit")
@Data
public class AdminUnit {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", length = 36)
    private String id;

    @Size(min = 1, max = 50)
    @NotEmpty(message = "Nama tidak boleh kosong")
    @Column(name = "nama", length = 50)
    private String nama;

    @Size(min = 1, max = 250)
    @NotEmpty(message = "Keterangan tidak boleh kosong")
    @Column(name = "keterangan", length = 250)
    private String keterangan;

    @ElementCollection
    @CollectionTable(schema = "presensi", name = "admin_unit_kerja", joinColumns = @JoinColumn(name = "id_admin_unit"))
    @Column(name = "id_unitkerja_presensi")
    private Set<UnitKerjaPresensi> unitKerja = new HashSet<>(); // cakupan unit kerjanya

    @ManyToOne
    @JoinColumn(name = "id_pegawai")
    private Pegawai pegawai; // untuk adminnya


    @NotEmpty(message = "Jabatan Penanggung Jawab tidak boleh kosong")
    @Column(name = "pj_jabatan", length = 30)
    private String pjJabatan; // untuk ttd

    @ManyToOne
    @JoinColumn(name = "pj_pegawai")
    private Pegawai pjPegawai; // untuk ttd nama

    public AdminUnit() {
    }

}
