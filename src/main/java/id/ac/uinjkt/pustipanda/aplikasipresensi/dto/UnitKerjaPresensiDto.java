package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Unit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitKerjaPresensiDto {
    private Long id;

    @NotBlank(message = "Nama Unit Kerja harus diisi")
    private String nama;

    private UnitKerjaPresensi parent;

    @NotNull(message = "Unit harus dipilih")
    private Unit unit;
}
