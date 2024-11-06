package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapAbsensiCsvPegawaiDto implements Comparable<RekapAbsensiCsvPegawaiDto> {
    private Long idPegawai;
    private LocalDate tanggal;
    private String nomorAbsen;
    private String nama;
    private String unitKerjaPresensi;
    private String waktuIn;
    private String waktuOut;
    private String jabatan;
    //private Boolean statusPegawaiShift = false;
    private String keterangan;

    @Override
    public int compareTo(RekapAbsensiCsvPegawaiDto o) {
        if (getTanggal() == null || o.getTanggal() == null)
            return 0;
        return getTanggal().compareTo(o.getTanggal());
    }
}
