package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresensiDto {
    private String pegawai;
    private Integer year;
    private Integer month;
    private Integer aktual;
    private Integer jumlah;
}
