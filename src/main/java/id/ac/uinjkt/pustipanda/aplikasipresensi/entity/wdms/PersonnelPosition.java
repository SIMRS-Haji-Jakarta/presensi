package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "personnel_position", schema = "public")
@Data
public class PersonnelPosition {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "position_code")
    private String positionCode;
    @Column(name = "position_name")
    private String positionName;
    @Column(name = "is_default")
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private PersonnelCompany companyId;
}
