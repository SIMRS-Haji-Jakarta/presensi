package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "User Id harus diisi")
    @Size(max = 255)
    private String userId;

    //@NotBlank(message = "Password harus diisi")
    @Size(max = 255)
    private String userPassword;

    @Size(max = 255)
    private String newPassword;

    @Size(max = 255)
    private String passwordConfirm;

    @NotNull(message = "Role harus dipilih")
    private Role role;

    @NotNull(message = "Pegawai harus dipilih")
    private Pegawai pegawai;

    private Boolean aktif = false;
}
