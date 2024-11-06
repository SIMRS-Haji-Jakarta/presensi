package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapHarianPegawaiDto {
    private Long idPegawai;
    private String nama;
    private String nip;
    private String jabatan;
    private String wfh; //wfh
    private String wfo; //
    private Integer sakit;
    private Integer cuti;
    private Integer dinasLuar;
    private Integer tugasBelajar; //
    private Integer tanpaKeterangan;
}
