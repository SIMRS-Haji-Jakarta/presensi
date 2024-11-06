package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personnel_employee", schema = "public")
@Data
public class PersonnelEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "first_name", length = 25)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "passport")
    private String passport;

    @Column(name = "driver_license_automobile")
    private String driverLicenseAutomobile;

    @Column(name = "driver_license_motorcycle")
    private String driverLicenseMotorcycle;

    @Column(name = "photo")
    private String photo;

    @Column(name = "self_password")
    private String selfPassword;

    @Column(name = "device_password")
    private String devicePassword;

    @Column(name = "dev_privilege")
    private Integer devPrivilege;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "acc_group")
    private String accGroup;

    @Column(name = "acc_timezone")
    private String accTimezone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "address")
    private String address;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "office_tel")
    private String officeTel;

    @Column(name = "contact_tel")
    private String contactTel;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "national")
    private String national;

    @Column(name = "religion")
    private String religion;

    @Column(name = "title")
    private String title;

    @Column(name = "enroll_sn")
    private String enrollSn;

    @Column(name = "ssn")
    private String ssn;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "verify_mode")
    private Integer verifyMode;

    @Column(name = "city")
    private String city;

    // @Column(name = "is_admin")
    // private Boolean isAdmin;

    @Column(name = "emp_type")
    private Short empType;

    @Column(name = "enable_payroll")
    private Boolean enablePayroll;

    // @Column(name = "deleted")
    // private Boolean deleted;

    // @Column(name = "reserved")
    // private Integer reserved;

    // @Column(name = "del_tag")
    // private Integer delTag;

    @Column(name = "app_status")
    private Short appStatus;

    @Column(name = "app_role")
    private Short appRole;

    @Column(name = "email")
    private String email;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "login_ip")
    private String loginIp;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private PersonnelCompany companyId;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private PersonnelDepartment departmentId;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private PersonnelPosition positionId;

    @Column(name = "id_pegawai")
    private Long pegawai;
}
