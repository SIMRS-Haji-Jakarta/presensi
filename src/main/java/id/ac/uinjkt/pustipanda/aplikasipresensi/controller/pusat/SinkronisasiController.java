package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FacePegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensiPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockBiodata;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.PersonnelEmployee;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/pusat/sinkronisasi")
public class SinkronisasiController {

    @Autowired
    private PersonnelEmployeeService personnelEmployeeService;

    @Autowired
    private IclockBioadataService iclockBioadataService;

    @Autowired
    private FacePegawaiService facePegawaiService;

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @Autowired
    private MesinPresensiPegawaiService mesinPresensiPegawaiService;

    @Autowired
    private SinkronisasiService sinkronisasiService;

    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm) {

        mm.addAttribute("listMesinPresensi", mesinPresensiService.findListAllMesinPresensi());

        return "pusat/sinkronisasi/list";
    }

    /*
    method pegawai ke mesin
    1. pilih mesin presensi
    2. aturan ke save table PersonnelEmployee
    2.1 kolom firstName, pake nama lengkap dgn gelar
    2.2 status=0
    2.3 empCode= no absen with numeric
    2.4 isAdmin=f
    2.5 enablePayroll=t
    2.6 deleted=f
    2.7 isActive=t
    2.8 companyId=1 (uinjkt)
    2.9  departmentId= table referensi.unit id (pastikan pas mapping ke presensi di cek juga)
    2.10 positionId= 1 || 2 (1=Dosen, 2=Tendik)
    2.11 verifyMode=15
    2.12 pegawai= id dari table Pegawai
    2.13 devPrivilege=0
    2.14 accGroup=1
    2.15 accTimezone=0000000100000000
    3. aturan ke save table PersonnelEmployeeArea
    3.1 areaId= kodeArea pada table MesinPresensi (1 pegawai bisa lebih dari 1 area)
     */
    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/proses-pegawai-mesin"})
    public String processPegawaiToMesin(@RequestParam(value = "mesinPresensi") MesinPresensi mesinPresensi,
                                        RedirectAttributes ra) {

        List<MesinPresensiPegawai> mesinPresensiPegawaiList = mesinPresensiPegawaiService.findAllByMesinPresensi(mesinPresensi);

        log.info("==== Proses pegawai ke mesin START ===");
        sinkronisasiService.prosesSinkronisasiPegawaiToMesin(mesinPresensiPegawaiList);

        ra.addFlashAttribute("messageOk", "Proses Pegawai ke Mesin Selesai");
        log.info("==== Proses Proses pegawai ke mesin END ===");

        return "redirect:/pusat/sinkronisasi/list";
    }

    /*
    method biodata ke presensi
    1. aturan save ke table FacePegawai
    1.1 ambil data dari table IclockBiodata
    1.2 pastikan hanya pegawai yg termapping yg bisa di save
     */
    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/proses-biodata-presensi"})
    public String processBiodataToPresensi(@RequestParam(value = "mesinPresensi") MesinPresensi mesinPresensi, RedirectAttributes ra) {

        List<IclockBiodata> iclockBiodataList = iclockBioadataService.findAll();

        log.info("==== Proses biodata ke presensi START ===");
        for (IclockBiodata data : iclockBiodataList) {
            Optional<MesinPresensiPegawai> mesinPresensiPegawai = mesinPresensiPegawaiService.findByPegawaiNomorAbsenAndMesinPresensi(data.getEmployeeId().getEmpCode(), mesinPresensi);
            if (mesinPresensiPegawai.isPresent()) {
                FacePegawai facePegawai = facePegawaiService.getByPegawai(mesinPresensiPegawai.get().getPegawai());
                if (facePegawai == null) {
                    facePegawai = new FacePegawai();
                    facePegawai.setPegawai(mesinPresensiPegawai.get().getPegawai());
                    facePegawai.setBioNo(data.getBioNo());
                    facePegawai.setBiodata(data.getBioTmp());
                    facePegawai.setBioIndex(data.getBioIndex());
                    facePegawai.setBioType(data.getBioType());
                    facePegawai.setDuress(data.getDuress());
                    facePegawai.setValid(data.getValid());
                    facePegawai.setMinorVer(data.getMinorVer());
                    facePegawai.setMajorVer(data.getMajorVer());

                    facePegawaiService.save(facePegawai);
                    log.info("processBiodataToPresensi -> save facePegawai id {}", facePegawai.getId());
                }
            } else {
                log.info("processBiodataToPresensi -> skip pegawai karena tidak terdaftar di MesinPresensiPegawai");
                continue;
            }
        }

        ra.addFlashAttribute("messageOk", "Proses Biodata ke Presensi Selesai");
        log.info("==== Proses biodata ke presensi END ===");

        return "redirect:/pusat/sinkronisasi/list";
    }


    /*
   method face ke mesin
   1. pilih pegawai yang akan di transfer ke mesin (bisa 1 pegawai, bisa beberapa pegawai)
   2. pilih mesin presensi
   3. aturan ke save IclockBiodata
   3.1 employeeId= id dari table PersonnelEmployee
   3.2 status=0
   3.3 bioTmp= biodata dari table FacePegawai
   3.4 bioType= bioType dari table FacePegawai
   3.5 majorVer= minorVer dari table FacePegawai
   3.6 valid=1
   3.7 duress=0
    */
    @PreAuthorize("hasAnyAuthority('am','pegpst','programmer')")
    @GetMapping(value = {"/proses-face-mesin"})
    public String processFaceToMesin(@RequestParam(value = "mesinPresensi") MesinPresensi mesinPresensi,
                                     @RequestParam(value = "facePegawais", required = false) List<FacePegawai> facePegawais,
                                     RedirectAttributes ra) {

        log.info("==== Proses face ke mesin START ===");
        if (facePegawais == null)
            facePegawais = new LinkedList<>();

        if (facePegawais.size() < 1 && mesinPresensi != null) {
            List<FacePegawai> facePegawaiList = facePegawaiService.findAllByMesinPresensi(mesinPresensi);
            if (facePegawaiList.size() > 0)
                facePegawais.addAll(facePegawaiList);
        }

        for (FacePegawai data : facePegawais) {
            PersonnelEmployee personnelEmployee = personnelEmployeeService.findByEmpCode(data.getPegawai().getNomorAbsen());
            if (personnelEmployee != null) {
                IclockBiodata iclockBiodata = iclockBioadataService.findFirstByEmployeeId(personnelEmployee);
                if (iclockBiodata == null) {
                    iclockBiodata = new IclockBiodata();

                    iclockBiodata.setEmployeeId(personnelEmployee);
                    iclockBiodata.setStatus((short) 0);
                    iclockBiodata.setBioTmp(data.getBiodata());
                    iclockBiodata.setBioType(data.getBioType());
                    iclockBiodata.setMinorVer(data.getMinorVer());
                    iclockBiodata.setMajorVer(data.getMajorVer());
                    iclockBiodata.setValid(1);
                    iclockBiodata.setDuress(0);

                    iclockBioadataService.save(iclockBiodata);
                    log.info("processFaceToMesin -> save iclockBiodata id {}", iclockBiodata.getId());
                }
            }
        }

        ra.addFlashAttribute("messageOk", "Proses Face ke Mesin Selesai");
        log.info("==== Proses face ke mesin END ===");
        return "redirect:/pusat/sinkronisasi/list";
    }
}
