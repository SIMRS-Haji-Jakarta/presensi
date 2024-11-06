package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoProfileDto {
    private String id;
    private Long idPegawai;
    private String nik;
    private String nip;
    private String nama;
    private String jenisKelamin;
    private String kategoriPegawai;
    private String unitKerjaPresensi;
    private String unit;
    private String urlBerkas;
    private Boolean status;
}
