package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapPresensiPegawaiAdminUnitDto {
    private Long idPegawai;
    private String noAbsen;
    private String nama;
    private String nip;
    private String golongan;
    private String jabatan;
    private String unitKerjaPresensi;
    private Integer tahun;
    private Integer bulan;
    private String bulanNama;
    private Integer hariKerja;
    private Integer hadir;
    private Integer cuti;
    private Integer izin;
    private Integer sakit;
    private Integer dinasLuar;
    private Integer tidakAbsen;
    private Integer alpa;
    private Long terlambat;
    private Long timeTerlambat;
    private Long kecepetan;
    private Long timeKecepetan;
    private Long jamKurang;
    private Long timeJamKurang;
    private Long jamKerja;
    private Integer hariKurang;
    private Integer aktualKerja;
}
