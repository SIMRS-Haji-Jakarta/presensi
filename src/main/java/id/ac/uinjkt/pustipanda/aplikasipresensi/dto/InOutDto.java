package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InOutDto {
    private LocalDate tanggal;
    private LocalTime datang;
    private LocalTime pulang;
    private String status;
    private String jamKerjaShift;
    private String hari;
}
