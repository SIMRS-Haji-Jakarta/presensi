package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iclock_terminal", schema = "public")
@Data
public class IclockTerminal {
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

    @Column(name = "sn")
    private String sn;

    @Column(name = "alias")
    private String alias;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "real_ip")
    private String realIp;

    @Column(name = "state")
    private int state;

    @Column(name = "terminal_tz")
    private short terminalTz;

    @Column(name = "heartbeat")
    private int heartbeat;

    @Column(name = "transfer_mode")
    private short transferMode;

    @Column(name = "transfer_interval")
    private int transferInterval;

    @Column(name = "transfer_time")
    private String transferTime;

    @Column(name = "product_type")
    private Short productType;

    @Column(name = "is_attendance")
    private short isAttendance;

    @Column(name = "is_registration")
    private short isRegistration;

    @Column(name = "purpose")
    private Short purpose;

    @Column(name = "controller_type")
    private Short controllerType;

    @Column(name = "authentication")
    private short authentication;

    @Column(name = "style")
    private String style;

    @Column(name = "upload_flag")
    private String uploadFlag;

    @Column(name = "fw_ver")
    private String fwVer;

    @Column(name = "push_protocol")
    private String pushProtocol;

    @Column(name = "push_ver")
    private String pushVer;

    @Column(name = "language")
    private Integer language;

    @Column(name = "is_tft")
    private Boolean isTft;

    @Column(name = "terminal_name")
    private String terminalName;

    @Column(name = "platform")
    private String platform;

    @Column(name = "oem_vendor")
    private String oemVendor;

    @Column(name = "log_stamp")
    private String logStamp;

    @Column(name = "op_log_stamp")
    private String opLogStamp;

    @Column(name = "capture_stamp")
    private String captureStamp;

    @Column(name = "user_count")
    private Integer userCount;

    @Column(name = "user_capacity")
    private Integer userCapacity;

    @Column(name = "photo_func_on")
    private Boolean photoFuncOn;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "transaction_capacity")
    private Integer transactionCapacity;

    @Column(name = "fp_func_on")
    private Boolean fpFuncOn;

    @Column(name = "fp_count")
    private Integer fpCount;

    @Column(name = "fp_capacity")
    private Integer fpCapacity;

    @Column(name = "fp_alg_ver")
    private String fpAlgVer;

    @Column(name = "face_func_on")
    private Boolean faceFuncOn;

    @Column(name = "face_count")
    private Integer faceCount;

    @Column(name = "face_capacity")
    private Integer faceCapacity;

    @Column(name = "face_alg_ver")
    private String faceAlgVer;

    @Column(name = "fv_func_on")
    private Boolean fvFuncOn;

    @Column(name = "fv_count")
    private Integer fvCount;

    @Column(name = "fv_capacity")
    private Integer fvCapacity;

    @Column(name = "fv_alg_ver")
    private String fvAlgVer;

    @Column(name = "palm_func_on")
    private Boolean palmFuncOn;

    @Column(name = "palm_count")
    private Integer palmCount;

    @Column(name = "palm_capacity")
    private Integer palmCapacity;

    @Column(name = "palm_alg_ver")
    private String palmAlgVer;

    @Column(name = "lock_func")
    private short lockFunc;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "push_time")
    private LocalDateTime pushTime;

    @Column(name = "is_access")
    private short isAccess;

    @Column(name = "area_id")
    private Integer areaId;

}
