package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.firebase;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionRequest {
    String topicName;
    List<String> tokens;
}
