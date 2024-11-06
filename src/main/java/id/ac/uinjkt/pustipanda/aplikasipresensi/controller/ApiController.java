package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.EmployeeDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FacePegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Area;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.AreaService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FacePegawaiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/employees")
public class ApiController {
    @Autowired
    private PegawaiDao pegawaiDao;

    @Autowired
    private AreaService areaService;

    @Autowired
    private FacePegawaiService facePegawaiService;

    @GetMapping
    public List<EmployeeDto> getAllEmployees(@RequestParam(value = "nama", required = false) String nama) {
        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployee();
        } else {
            return pegawaiDao.findAllEmployeeByName(nama.toLowerCase());
        }
    }

    @GetMapping(value = "/unit-kerja-presensi/{unitkerja}")
    public List<EmployeeDto> getAllEmployeesUnitKerjaPresensi(@PathVariable("unitkerja") UnitKerjaPresensi unitKerjaPresensi,
                                                              @RequestParam(value = "nama", required = false) String nama) {
        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeByUnitKerjaPresensi(unitKerjaPresensi.getId());
        } else {
            return pegawaiDao.findAllEmployeeByUnitKerjaPresensiAndName(unitKerjaPresensi.getId(), nama.toLowerCase());
        }
    }

    @GetMapping("/dosen")
    public List<EmployeeDto> getAllEmployeesDosen(@RequestParam(value = "nama", required = false) String nama) {
        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeDosen();
        } else {
            return pegawaiDao.findAllEmployeeDosenByName(nama.toLowerCase());
        }
    }

    @GetMapping(value = "/mesin-presensi/{mesinpresensi}")
    public List<FacePegawaiDto> getAllEmployeesPerMesinPresensi(@PathVariable("mesinpresensi") MesinPresensi mesinPresensi, @RequestParam(value = "nama", required = false) String nama) {
        log.info("mesinPresensi: {}, nama: {}", mesinPresensi.getNama(), nama);
        if (nama == null) {
            return facePegawaiService.findAllFacePegawaiDtoByMesinPresensi(mesinPresensi);
        } else {
            return facePegawaiService.findAllFacePegawaiDtoByMesinPresensiAndName(mesinPresensi, nama.toLowerCase());
        }
    }

    @GetMapping("/shift")
    public List<EmployeeDto> getAllEmployeesShift(@RequestParam(value = "nama", required = false) String nama) {
        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeShift();
        } else {
            return pegawaiDao.findAllEmployeeShiftByName(nama.toLowerCase());
        }
    }

    @GetMapping("/shift/unit-kerja-presensi/{unitkerja}")
    public List<EmployeeDto> getAllEmployeesShiftUnitKerjaPresensi(@PathVariable("unitkerja") UnitKerjaPresensi unitKerjaPresensi,
                                                                   @RequestParam(value = "nama", required = false) String nama) {

        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeShiftByUnitKerjaPresensi(unitKerjaPresensi.getId());
        } else {
            return pegawaiDao.findAllEmployeeShiftByUnitKerjaPresensiAndNama(unitKerjaPresensi.getId(), nama.toLowerCase());
        }
    }


    @GetMapping("/shift/{unit}")
    public List<EmployeeDto> getAllEmployeesShiftByAdminUnit(@PathVariable(value = "unit") String unit,
                                                             @RequestParam(value = "nama", required = false) String nama) {
        log.info("nama: {}", nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeShiftByUnit(unit);
        } else {
            return pegawaiDao.findAllEmployeeShiftByUnitByName(unit, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/unit/{unit}")
    public List<EmployeeDto> getAllEmployees(@PathVariable("unit") String unit, @RequestParam(value = "nama", required = false) String nama) {
        log.info("unit: {}, nama: {}", unit, nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeByUnit(unit);
        } else {
            return pegawaiDao.findAllEmployeeByUnitByName(unit, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/admin-pusat/unit/{unit}")
    public List<EmployeeDto> getAllEmployeesPerUnit(@PathVariable("unit") Long unit, @RequestParam(value = "nama", required = false) String nama) {
        log.info("unit: {}, nama: {}", unit, nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeAdminPusatByUnit(unit);
        } else {
            return pegawaiDao.findAllEmployeeAdminPusatByUnitByName(unit, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/admin-pusat/kategori/{kategori}")
    public List<EmployeeDto> getAllEmployeesPerKategori(@PathVariable("kategori") Long kategori, @RequestParam(value = "nama", required = false) String nama) {
        log.info("kategori: {}, nama: {}", kategori, nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeAdminPusatByKategori(kategori);
        } else {
            return pegawaiDao.findAllEmployeeAdminPusatByKategoriByName(kategori, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/admin-pusat/unit/{unit}/kategori/{kategori}")
    public List<EmployeeDto> getAllEmployeesPerUnitPerKategori(@PathVariable("unit") Long unit, @PathVariable("kategori") Long kategori,
                                                               @RequestParam(value = "nama", required = false) String nama) {
        log.info("unit: {}, nama: {}, kateogri: {}", unit, nama, kategori);
        if (nama == null && kategori == null) {
            return pegawaiDao.findAllEmployeeAdminPusatByUnit(unit);
        } else if (kategori != null && nama == null) {
            return pegawaiDao.findAllEmployeeAdminPusatByUnitByKategori(unit, kategori);
        } else if (kategori == null) {
            return pegawaiDao.findAllEmployeeAdminPusatByUnitByName(unit, nama.toLowerCase());
        } else
            return pegawaiDao.findAllEmployeeAdminPusatByUnitByKategoriByName(unit, kategori, nama.toLowerCase());
    }

    @GetMapping(value = "/admin-pusat/unit/dosen/{unit}")
    public List<EmployeeDto> getAllEmployeesDosenPerUnit(@PathVariable("unit") Long unit, @RequestParam(value = "nama", required = false) String nama) {
        log.info("unit: {}, nama: {}", unit, nama);
        if (nama == null) {
            return pegawaiDao.findAllEmployeeDosenAdminPusatByUnit(unit);
        } else {
            return pegawaiDao.findAllEmployeeDosenAdminPusatByUnitByName(unit, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/admin-unit/{listIdUnitKerja}")
    public List<EmployeeDto> getAllEmployees(@PathVariable("listIdUnitKerja") List<Long> listIdUnitKerja, @RequestParam(value = "nama", required = false) String nama) {
        log.info("list id unit kerja: {}", listIdUnitKerja);

        if (nama == null) {
            return pegawaiDao.findAllEmployeeByListUnitKerja(listIdUnitKerja);
        } else {
            return pegawaiDao.findAllEmployeeByListUnitKerjaByName(listIdUnitKerja, nama.toLowerCase());
        }
    }

    @GetMapping(value = "/area-unit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<Area>> getJsonCoordinate() {

        List<Area> listAreaUnit = areaService.getAreaList();
        log.info("Ukuran list Area {}", listAreaUnit.size());

        final Map<String, List<Area>> m = new HashMap<>();
        m.put("areaunit", listAreaUnit);

        return m;
    }
}
