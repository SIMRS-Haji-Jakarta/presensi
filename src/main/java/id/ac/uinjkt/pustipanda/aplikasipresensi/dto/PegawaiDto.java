package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PegawaiDto {
    private Long id;

    @NotBlank(message = "Nama Unit Kerja harus diisi")
    private String nama;

    private String gelarDepan;

    private String gelarBelakang;

    @NotBlank(message = "Jenis kelamin harus diisi")
    private String jenisKelamin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal lahir harus diisi")
    private LocalDate tanggalLahir;

    @NotBlank(message = "Nomor absen harus diisi")
    private String nomorAbsen;

    private Boolean isFace = Boolean.TRUE;

    @NotBlank(message = "NIP atau Nomor Registrasi Pegawai harus diisi")
    private String nip;

    @NotBlank(message = "Nomor KTP harus diisi")
    private String nik;

    private String spesialisasi1;

    private String uuid;

    @NotNull(message = "Jenis Pegawai harus dipilih")
    private JenisPegawai jenisPegawai;

    @NotNull(message = "Status Pegawai harus dipilih")
    private StatusPegawai statusPegawai;

    @NotNull(message = "Unit Kerja harus dipilih")
    private UnitKerjaPresensi unitKerjaPresensi;

    @NotNull(message = "Jabatan harus dipilih")
    private JabatanPegawai jabatanPegawai;

    @NotNull(message = "Golongan harus dipilih")
    private Golongan golongan;
}
