package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapPresensiPegawaiDto {
    private Integer tahun;
    private Integer bulan;
    private String bulanNama;
    private Integer hariKerja;
    private Integer hadir;
    private Integer cuti;
    private Integer sakit;
    private Integer dinasLuar;
    private Integer tidakAbsen;
    private Integer alpa;
    private LocalTime terlambat;
    private Long timeTerlambat;
    private LocalTime kecepetan;
    private Long timeKecepetan;
    private LocalTime jamKurang;
    private Long timeJamKurang;
    private Integer hariKurang;
    private Integer aktualKerja;
    private Long jamKerja;
}
