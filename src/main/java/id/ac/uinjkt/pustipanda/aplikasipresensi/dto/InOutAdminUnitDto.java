package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InOutAdminUnitDto {
    private LocalDate tanggal;
    private LocalTime datang;
    private LocalTime pulang;
    private Pegawai pegawai;
    private String status;
    private String jamKerjaShift;
    private String hari;
}
