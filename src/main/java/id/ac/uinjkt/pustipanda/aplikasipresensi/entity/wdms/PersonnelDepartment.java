package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "personnel_department", schema = "public")
@Data
public class PersonnelDepartment {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "dept_code")
    private String deptCode;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private PersonnelCompany companyId;
}
