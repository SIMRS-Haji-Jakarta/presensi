package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployeeArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SinkronisasiService {
    @Autowired
    private PersonnelEmployeeService personnelEmployeeService;

    @Autowired
    private PersonnelCompanyService personnelCompanyService;

    @Autowired
    private PersonnelDepartmentService personnelDepartmentService;

    @Autowired
    private PersonnelPositionService personnelPositionService;

    @Autowired
    private PersonnelEmployeeAreaService personnelEmployeeAreaService;

    @Autowired
    private PersonnelAreaService personnelAreaService;

    public void prosesSinkronisasiPegawaiToMesin(List<MesinPresensiPegawai> mesinPresensiPegawaiList) {
        for (MesinPresensiPegawai data : mesinPresensiPegawaiList) {
            PersonnelEmployee personnelEmployee = personnelEmployeeService.findByEmpCode(data.getPegawai().getNomorAbsen());
            if (personnelEmployee == null) {
                personnelEmployee = new PersonnelEmployee();

                setData(personnelEmployee, data);
                personnelEmployeeService.save(personnelEmployee);
                log.info("processPegawaiToMesin -> save personnelEmployee id {}", personnelEmployee.getId());
            }

            if (personnelEmployee != null) {
                if (personnelEmployee.getPegawai() == null) {
                    //Jika pegawai diinput lewat mesin presensi terlebih dulu, maka updata data nya
                    setData(personnelEmployee, data);
                    personnelEmployeeService.save(personnelEmployee);
                    log.info("processPegawaiToMesin -> update personnelEmployee id {}", personnelEmployee.getId());
                }

                //save ke PersonnelEmployeeArea
                PersonnelEmployeeArea personnelEmployeeArea = personnelEmployeeAreaService.findByAreaCodeAndEmployeeId(String.valueOf(data.getMesinPresensi().getKodeArea()), personnelEmployee);
                if (personnelEmployeeArea == null) {
                    personnelEmployeeArea = new PersonnelEmployeeArea();
                    personnelEmployeeArea.setEmployeeId(personnelEmployee);
                    personnelEmployeeArea.setAreaId(personnelAreaService.findByAreaCode(String.valueOf(data.getMesinPresensi().getKodeArea())));
                    personnelEmployeeAreaService.save(personnelEmployeeArea);
                    log.info("processPegawaiToMesin -> save personnelEmployeeArea id {}", personnelEmployeeArea.getId());
                }
            }
        }
    }

    private void setData(PersonnelEmployee personnelEmployee, MesinPresensiPegawai data) {
        personnelEmployee.setFirstName(Pegawai.toFirstName(data.getPegawai()));
        personnelEmployee.setStatus((short) 0);
        personnelEmployee.setEmpCode(data.getPegawai().getNomorAbsen());
        // personnelEmployee.setIsAdmin(false);
        personnelEmployee.setEnablePayroll(true);
       // personnelEmployee.setDeleted(false);
        personnelEmployee.setIsActive(true);
        personnelEmployee.setCompanyId(personnelCompanyService.getDefaultCompany()); //uinjkt
        personnelEmployee.setDepartmentId(personnelDepartmentService.getDataByUnit(data.getPegawai().getUnitKerjaPresensi().getUnit()));
        personnelEmployee.setPositionId(personnelPositionService.getDataByPegawai(data.getPegawai()));
        personnelEmployee.setVerifyMode(15);
        personnelEmployee.setPegawai(data.getPegawai().getId());
        personnelEmployee.setDevPrivilege(0);
        personnelEmployee.setAccGroup(String.valueOf(1));
        personnelEmployee.setAccTimezone("0000000100000000");
    }
}
