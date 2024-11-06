package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MesinPresensiUnitDto {
    private Long id;

    @NotNull(message = "Mesin presensi harus dipilih")
    private MesinPresensi mesinPresensi;

    @NotNull(message = "Admin unit harus dipilih")
    private AdminUnit adminUnit;
}
