package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MesinPresensiDto {
    private Long id;

    @NotBlank(message = "Nama harus diisi")
    @Size(max = 255)
    private String nama;

    @NotBlank(message = "Lokasi harus diisi")
    @Size(max = 200)
    private String lokasi;

    @NotBlank(message = "IP Address harus diisi")
    @Size(max = 50)
    private String ipAddress;

    @NotNull(message = "Kode area tidak boleh kosong")
    private Integer kodeArea;

    @NotBlank(message = "SN harus diisi")
    @Size(max = 50)
    private String sn;
}
