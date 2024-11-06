package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "ep_eptransaction", schema = "public")
@Data
public class EpEptransaction {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "change_time")
    private LocalDateTime changeTime;

    @Column(name = "change_user")
    private String changeUser;

    @Column(name = "status")
    private short status;

    @Column(name = "area")
    private String area;

    @Column(name = "check_datetime")
    private LocalDateTime checkDatetime;

    @Column(name = "check_date")
    private LocalDate checkDate;

    @Column(name = "check_time")
    private LocalTime checkTime;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "is_mask")
    private int isMask;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "source")
    private short source;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private PersonnelEmployee empId;

    @ManyToOne
    @JoinColumn(name = "terminal_id")
    private IclockTerminal terminalId;

    @Column(name = "`temperature_F`")
    private BigDecimal temperatureF;

    @Column(name = "terminal_sn")
    private String terminalSn;

    @Column(name = "is_sync")
    private Boolean isSync;
}
