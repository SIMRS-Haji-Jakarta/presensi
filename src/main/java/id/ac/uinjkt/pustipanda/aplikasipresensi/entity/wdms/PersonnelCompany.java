package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "personnel_company", schema = "public")
@Data
public class PersonnelCompany {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "is_default")
    private Boolean isDefault;
}
