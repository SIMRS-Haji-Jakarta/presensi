package id.ac.uinjkt.pustipanda.aplikasipresensi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationError {
    private String field;
    private String code;
    private String defaultMessage;
}
