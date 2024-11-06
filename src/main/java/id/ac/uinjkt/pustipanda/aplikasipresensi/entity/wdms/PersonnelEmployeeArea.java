package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "personnel_employee_area", schema = "public")
@Data
public class PersonnelEmployeeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private PersonnelEmployee employeeId;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private PersonnelArea areaId;
}
