package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.PegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.StatusPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.UnitKerjaPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/pusat/pegawai")
public class PegawaiController {

    @Autowired
    private PegawaiService pegawaiService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UnitKerjaPresensiService unitKerjaPresensiService;

    @Autowired
    private StatusPegawaiSevice statusPegawaiSevice;

    @Autowired
    private JenisPegawaiService jenisPegawaiService;

    @Autowired
    private JabatanPegawaiService jabatanPegawaiService;

    @Autowired
    private GolonganSevice golonganService;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "q", required = false) String search,
                           @RequestParam(value = "unitKerjaPresensi", required = false) UnitKerjaPresensi unitKerjaPresensi,
                           @RequestParam(value = "statusPegawai", required = false) StatusPegawai statusPegawai,
                           @PageableDefault(size = 10) Pageable page) {
        Page<Pegawai> result = pegawaiService.getAllDataBySearchParam(search, unitKerjaPresensi, statusPegawai, page);
        log.info("search: {}", search);

        mm.addAttribute("q", search);
        mm.addAttribute("unitKerjaPresensi", unitKerjaPresensi);
        mm.addAttribute("statusPegawai", statusPegawai);
        mm.addAttribute("listUnitKerja", unitKerjaPresensiService.findAll());
        mm.addAttribute("listStatusPegawai", statusPegawaiSevice.findAll());
        mm.addAttribute("data", result);

        return AppConstant.TEMPLATE_PEGAWAI;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        Pegawai unitKerjaPresensi = new Pegawai();
        if (id != null && !id.isEmpty()) {
            Optional<Pegawai> o = pegawaiService.findById(Long.valueOf(id));
            if (o.isPresent()) {
                unitKerjaPresensi = o.get();
            }
        }

        mm.addAttribute("data", unitKerjaPresensi.getId() == null ? new PegawaiDto() : unitKerjaPresensi);
        mm.addAttribute("listUnitKerja", unitKerjaPresensiService.findAll());
        mm.addAttribute("listStatusPegawai", statusPegawaiSevice.findAll());
        mm.addAttribute("listJenisPegawai", jenisPegawaiService.findAll());
        mm.addAttribute("listJabatanPegawai", jabatanPegawaiService.findAll());
        mm.addAttribute("listGolongan", golonganService.findAll());
        mm.addAttribute("listJenisKelamin", pegawaiService.getAllJenisKelamin());

        return AppConstant.TEMPLATE_PEGAWAI + " :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid PegawaiDto o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {
        log.info("error : {}", errors);

        if (errors.hasErrors()) {
            log.info("errors {}", errors);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute(AppConstant.MESSAGE, err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return AppConstant.LINK_PEGAWAI;
        }

        if (o.getId() == null)
            o.setId(0L);

        Optional<Pegawai> oPegawai = pegawaiService.findById(o.getId());
        if (oPegawai.isPresent()) {
            if (pegawaiService.isDuplicate(o.getNip(), o.getNik(), o.getNomorAbsen(), o.getId())) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_PEGAWAI;
            }

            BeanUtils.copyProperties(o, oPegawai.get(), "id");
            pegawaiService.save(oPegawai.get());
        } else {
            if (pegawaiService.isDuplicate(o.getNip(), o.getNik(), o.getNomorAbsen(), null)) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");

                return AppConstant.LINK_PEGAWAI;
            }
//            Optional<Pegawai> checkPegawaiIsExist = pegawaiService.findByNipOrNikOrNomorAbsen(o.getNip(), o.getNik(), o.getNomorAbsen());
//            if (checkPegawaiIsExist.isPresent()) {
//                ra.addFlashAttribute(AppConstant.MESSAGE, "Data sudah ada.");
//
//                return AppConstant.LINK_PEGAWAI;
//            }

            Pegawai pegawai = new Pegawai();
            BeanUtils.copyProperties(o, pegawai);
            pegawaiService.save(pegawai);
        }

        return AppConstant.LINK_PEGAWAI;
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) Long id, RedirectAttributes ra) {
        Optional<Pegawai> pegawai = pegawaiService.findById(id);
        if (pegawai.isPresent()) {
            try {
                pegawaiService.delete(pegawai.get());
            } catch (Exception e) {
                log.error("error delete pegawai: {}", e.getMessage());
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak dapat dihapus");
            }

        } else {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak ditemukan");
        }

        return AppConstant.LINK_PEGAWAI;
    }
}
