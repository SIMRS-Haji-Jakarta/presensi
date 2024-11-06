package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.firebase;

import lombok.Data;

@Data
public class NotificationRequest {
    private String target;
    private String title;
    private String body;
}
