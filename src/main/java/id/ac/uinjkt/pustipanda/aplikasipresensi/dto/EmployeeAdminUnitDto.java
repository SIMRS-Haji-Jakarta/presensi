package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAdminUnitDto {
    private Long id;
    private String nip;
    private String nama;
    private String golongan;
    private String jabatan;
}
