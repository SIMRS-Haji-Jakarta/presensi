package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacePegawaiDto {
    private String id;
    private String gelarDepan;
    private String nama;
    private String gelarBelakang;
}
