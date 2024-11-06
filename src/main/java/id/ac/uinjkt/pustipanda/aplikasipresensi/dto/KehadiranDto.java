package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KehadiranDto {
    private String nama;
    private String gelarDepan;
    private String gelarBelakang;
    private String nip;
    private Double latitude;
    private Double longitude;
    private UnitKerjaPresensi unitKerja;
}
