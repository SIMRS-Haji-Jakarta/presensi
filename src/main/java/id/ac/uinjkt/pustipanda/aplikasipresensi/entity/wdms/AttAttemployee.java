package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "att_attemployee", schema = "public")
@Data
public class AttAttemployee {
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

    @Column(name = "enable_attendance")
    private Boolean enableAttendance;

    @Column(name = "enable_schedule")
    private Boolean enableSchedule;

    @Column(name = "enable_overtime")
    private Boolean enableOvertime;

    @Column(name = "enable_holiday")
    private Boolean enableHoliday;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private PersonnelEmployee empId;

    @Column(name = "group_id")
    private Integer groupId;
}
