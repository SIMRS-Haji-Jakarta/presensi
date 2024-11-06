package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "transaction_biotime", schema = "public")
@Data
public class TransactionPresensi {

//    @Id
//    @Column(name = "id_eptransaction")
//    private Integer idEptransaction;

    @Id
    @Column(name = "iclock_transaction")
    private Integer iclockTransaction;

    @Column(name = "no_absen")
    private String noAbsen;

    @Column(name = "in_out")
    private short inOut;

    @Column(name = "date_full")
    private LocalDateTime dateFull;

    @Column(name = "date_date")
    private LocalDate dateDate;

    @Column(name = "date_time")
    private LocalTime dateTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_sync")
    private Boolean isSync;

    @Column(name = "pegawai")
    private Long pegawai;
}
