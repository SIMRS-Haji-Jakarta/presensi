package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapKetidakhadiranPegawaiAdminUnitDto implements Comparable<RekapKetidakhadiranPegawaiAdminUnitDto> {
    private Long idPegawai;
    private LocalDate tanggal;
    private String nama;
    private String nip;
    private String golongan;
    private String jabatan;
    private String kategoriPegawai;
    private String jenisKelamin;
    private String unitKerjaPresensi;
    private Boolean statusPegawaiShift = false;
    private String status;

    @Override
    public int compareTo(RekapKetidakhadiranPegawaiAdminUnitDto o) {
        if (getTanggal() == null || o.getTanggal() == null)
            return 0;
        return getTanggal().compareTo(o.getTanggal());
    }
}
