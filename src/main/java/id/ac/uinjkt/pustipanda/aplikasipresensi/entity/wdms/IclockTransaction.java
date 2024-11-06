package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "iclock_transaction", schema = "public")
@Data
public class IclockTransaction {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "punch_time")
    private LocalDateTime punchTime;

    @Column(name = "punch_state")
    private String punchState;

    @Column(name = "verify_type")
    private int verifyType;

    @Column(name = "work_code")
    private String workCode;

    @Column(name = "terminal_sn")
    private String terminalSn;

    @Column(name = "terminal_alias")
    private String terminalAlias;

    @Column(name = "area_alias")
    private String areaAlias;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "gps_location")
    private String gpsLocation;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "source")
    private Short source;

    @Column(name = "purpose")
    private Short purpose;

    @Column(name = "crc")
    private String crc;

    @Column(name = "is_attendance")
    private Short isAttendance;

    @Column(name = "reserved")
    private String reserved;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "sync_status")
    private Short syncStatus;

    @Column(name = "sync_time")
    private LocalDateTime syncTime;

    @Column(name = "is_mask")
    private Short isMask;

    @Column(name = "temperature")
    private BigDecimal temperature;

    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "terminal_id")
    private Integer terminalId;

    @Column(name = "is_sync")
    private Boolean isSync;
}
