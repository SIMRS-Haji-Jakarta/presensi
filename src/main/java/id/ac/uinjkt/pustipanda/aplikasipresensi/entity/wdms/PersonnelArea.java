package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "personnel_area", schema = "public")
@Data
public class PersonnelArea {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "area_name")
    private String areaName;

    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private PersonnelCompany companyId;

    @Column(name = "parent_area_id")
    private Integer parentAreaId;
}
