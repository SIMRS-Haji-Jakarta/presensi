package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDto {
    private Long id;

    @NotBlank(message = "Nama Unit harus diisi")
    private String nama;
}
