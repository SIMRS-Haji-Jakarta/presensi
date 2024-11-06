package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.InOutDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.KehadiranDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.RekapPresensiPegawaiDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.UserDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.*;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.AbsensiUtils;
import id.ac.uinjkt.pustipanda.aplikasipresensi.util.PegawaiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/presensi")
@Slf4j
public class PresensiController {

    @Autowired
    private CurrentService currentService;

    @Autowired
    private AbsensiDao absensiDao;

    @Autowired
    private PengajuanDao pengajuanDao;

    @Autowired
    private PengaduanDao pengaduanDao;

    @Autowired
    private StatusPresensiDao statusPresensiDao;

    @Autowired
    private HariKerjaDao hariKerjaDao;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FilePengajuanService filePengajuanService;

    @Autowired
    private AbsensiUtils absensiUtils;

    @Autowired
    private PegawaiUtils pegawaiUtils;

    @Autowired
    private JadwalKerjaShiftDao jadwalKerjaShiftDao;

    @Autowired
    private KonfigurasiService konfigurasiService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/datang")
    public String datangGet(ModelMap mm, Authentication auth) {
        Date date = Calendar.getInstance().getTime();
        mm.addAttribute("date", date);

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Konfigurasi konfigurasiAbsenByArea = konfigurasiService.getKonfigurasi(AppConstant.ABSEN_BY_AREA);
        //Optional<Absensi> oAbsensi = absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now());
        Absensi absensi = Optional.ofNullable(absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now()))
                .orElse(new Absensi(pegawai, LocalDate.now()));

        //pegawai.setIsFace(false); // mengaktifkan wfh 16,17 April 2024
        mm.addAttribute("pegawai", pegawai);
        mm.addAttribute("absensi", absensi);
        mm.addAttribute("absen_by_area", konfigurasiAbsenByArea != null
                && konfigurasiAbsenByArea.getNilai().equals(AppConstant.AKTIF));

        return "presensi/datang";
    }

    @PostMapping("/datang")
    public String datangPost(Authentication auth, HttpServletRequest request, RedirectAttributes ra, @RequestParam Double lat, @RequestParam Double lgn) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Absensi absensi = Optional.ofNullable(absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now()))
                .orElse(new Absensi(pegawai, LocalDate.now()));

        if (lat == null) {
            ra.addFlashAttribute("message", "Posisi Latitude tidak boleh kosong.");
            return "redirect:/presensi/datang";
        }

        if (lgn == null) {
            ra.addFlashAttribute("message", "Posisi Longitude tidak boleh kosong.");
            return "redirect:/presensi/datang";
        }
        log.info("datang Latitude: {}, Longitude: {}", lat, lgn);

        List<Konfigurasi> listKonfigurasiAbsenByArea = konfigurasiService.getListByNamaAndNilai(AppConstant.ABSEN_BY_AREA, AppConstant.AKTIF);
        if (listKonfigurasiAbsenByArea.size() > 0) {
            List<Area> listArea;
            boolean isInsidePolygon = false;
            listArea = areaService.getAreaList();
            for (Area a : listArea) {
                CheckInPolygonService oCheck = new CheckInPolygonService(lat, lgn, a.getKoordinat());
                isInsidePolygon = oCheck.isInPolygon(); //mengecek bahwa di dalam area di sisi backend
                if (isInsidePolygon)
                    break;
            }

            if (!isInsidePolygon) {
                ra.addFlashAttribute("message", "Oops! Pastikan Anda berada di area UIN Jakarta.");
                return "redirect:/presensi/datang";
            }
            log.info("Dalam area? {}", isInsidePolygon);
        }

        if (absensi.getWaktuIn() == null) {
            absensi.setWaktuIn(LocalTime.now());
            absensi.setWaktuInIp(request.getRemoteAddr());
            absensi.setWaktuInLat(lat);
            absensi.setWaktuInLgn(lgn);
            if (absensi.getStatusPresensi() == null) {
                absensi.setStatusPresensi(AppConstant.STATUS_HADIR);
            }

            //pegawai.setIsFace(false); // mengaktifkan wfh 16,17 April 2024
            if (pegawai.getIsFace() == null || !pegawai.getIsFace()) {
                absensiDao.save(absensi);
                ra.addFlashAttribute("infoDatang", "Presensi Datang berhasil dilakukan.");
            } else {
                ra.addFlashAttribute("message", "Silahkan melakukan pada Mesin Presensi.");
            }
        } else {
            ra.addFlashAttribute("message", "Anda sudah melakukan Presensi Datang.");
        }
        log.info("absensi: {}", absensi.getPegawai());

        return "redirect:/presensi/datang";
    }

    @GetMapping("/pulang")
    public String pulangGet(ModelMap mm, Authentication auth) {
        Date date = Calendar.getInstance().getTime();
        mm.addAttribute("date", date);

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Konfigurasi konfigurasiAbsenByArea = konfigurasiService.getKonfigurasi(AppConstant.ABSEN_BY_AREA);
        //Optional<Absensi> oAbsensi = absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now());
        Absensi absensi = Optional.ofNullable(absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now()))
                .orElse(new Absensi(pegawai, LocalDate.now()));

        //pegawai.setIsFace(false); // mengaktifkan wfh 16,17 April 2024
        mm.addAttribute("pegawai", pegawai);
        mm.addAttribute("absensi", absensi);
        mm.addAttribute("absen_by_area", konfigurasiAbsenByArea != null
                && konfigurasiAbsenByArea.getNilai().equals(AppConstant.AKTIF));

        return "presensi/pulang";
    }

    @PostMapping("/pulang")
    public String pulangPost(Authentication auth, HttpServletRequest request, RedirectAttributes ra, @RequestParam Double lat, @RequestParam Double lgn) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);

        if (lat == null) {
            ra.addFlashAttribute("message", "Posisi Latitude tidak boleh kosong.");
            return "redirect:/presensi/pulang";
        }

        if (lgn == null) {
            ra.addFlashAttribute("message", "Posisi Longitude tidak boleh kosong.");
            return "redirect:/presensi/pulang";
        }
        log.info("pulang Latitude: {}, Longitude: {}", lat, lgn);

        Absensi absensi = Optional.ofNullable(absensiDao.findByPegawaiAndTanggal(pegawai, LocalDate.now()))
                .orElse(new Absensi(pegawai, LocalDate.now()));

        List<Konfigurasi> listKonfigurasiAbsenByArea = konfigurasiService.getListByNamaAndNilai(AppConstant.ABSEN_BY_AREA, AppConstant.AKTIF);
        if (listKonfigurasiAbsenByArea.size() > 0) {
            List<Area> listArea;
            boolean isInsidePolygon = false;
            listArea = areaService.getAreaList();
            for (Area a : listArea) {
                CheckInPolygonService oCheck = new CheckInPolygonService(lat, lgn, a.getKoordinat());
                isInsidePolygon = oCheck.isInPolygon(); //mengecek bahwa di dalam area di sisi backend
                if (isInsidePolygon)
                    break;
            }

            if (!isInsidePolygon) {
                ra.addFlashAttribute("message", "Oops! Pastikan Anda berada di area UIN Jakarta.");
                return "redirect:/presensi/pulang";
            }
            log.info("Dalam area? {}", isInsidePolygon);
        }

        //Periksa pegawai shift
        if (pegawaiUtils.isPegawaiShift(pegawai)) {
            LocalDate tanggalShiftMalam = absensi.getTanggal().minusDays(1L);
            //periksa jadwal pegawai shift malam
            Optional<JadwalKerjaShift> jadwalKerjaShiftMalam = jadwalKerjaShiftDao.findFirstByPegawaiAndTanggalAndJamKerjaShift(pegawai, tanggalShiftMalam, AppConstant.JAM_KERJA_SHIFT_MALAM);
            if (jadwalKerjaShiftMalam.isPresent()) {

                Absensi absensiShiftMalam = Optional.ofNullable(absensiDao.findByPegawaiAndTanggal(pegawai, tanggalShiftMalam))
                        .orElse(new Absensi(pegawai, tanggalShiftMalam));

                absensiShiftMalam.setTanggalOut(LocalDate.now());

                /* pastikan waktu out shift malam tidak melebihi jam kerja datang shift berikutnya di tanggal berlangsung */

                Optional<JadwalKerjaShift> jadwalKerjaShiftLain = jadwalKerjaShiftDao.findFirstByPegawaiAndTanggalAndJamKerjaShiftIsNot(pegawai, absensi.getTanggal(), AppConstant.JAM_KERJA_SHIFT_LIBUR);
                LocalTime waktuOutShiftMalam = LocalTime.now();
                if (jadwalKerjaShiftLain.isPresent()) {
                    LocalTime jamKerjaDatangShiftLain = jadwalKerjaShiftLain.get().getJamKerjaShift().getDatang();
                    if (LocalTime.now().isAfter(jamKerjaDatangShiftLain))
                        waktuOutShiftMalam = jamKerjaDatangShiftLain;
                }

                /*-----*/

                absensiShiftMalam.setWaktuOut(waktuOutShiftMalam);
                absensiShiftMalam.setWaktuOutIp(request.getRemoteAddr());
                absensiShiftMalam.setWaktuOutLat(lat);
                absensiShiftMalam.setWaktuOutLgn(lgn);
                if (absensiShiftMalam.getStatusPresensi() == null) {
                    absensiShiftMalam.setStatusPresensi(AppConstant.STATUS_HADIR);
                }

                absensiDao.save(absensiShiftMalam);
            } else
                absensi.setTanggalOut(absensi.getTanggal());
        }

        // change absensi pulang to repeatly
        //if (absensi.getWaktuOut() == null) {
        absensi.setWaktuOut(LocalTime.now());
        absensi.setWaktuOutIp(request.getRemoteAddr());
        absensi.setWaktuOutLat(lat);
        absensi.setWaktuOutLgn(lgn);
        if (absensi.getStatusPresensi() == null) {
            absensi.setStatusPresensi(AppConstant.STATUS_HADIR);
        }

        //pegawai.setIsFace(false); // mengaktifkan wfh 16,17 April 2024
        if (pegawai.getIsFace() == null || !pegawai.getIsFace()) {
            absensiDao.save(absensi);
            ra.addFlashAttribute("infoPulang", "Presensi Pulang berhasil dilakukan.");
        } else {
            ra.addFlashAttribute("message", "Silahkan melakukan pada Mesin Presensi.");
        }

        return "redirect:/presensi/pulang";
    }


    @GetMapping("/kehadiran")
    public String kehadiranGet(ModelMap mm, Authentication auth, @PageableDefault(size = 10) Pageable page) {
        User user = currentService.getCurrentUser(auth);
        LocalDate tanggal = LocalDate.now();
        //List<KehadiranDto> kehadiranDtos = null;
        Page<KehadiranDto> result = new PageImpl<>(new LinkedList<>());
        int totalData;

        if (user.getRole().getRoleId().equals(AppConstant.ID_REKTOR) ||
                user.getRole().getRoleId().equals(AppConstant.ID_WAREK_1) ||
                user.getRole().getRoleId().equals(AppConstant.ID_WAREK_2) ||
                user.getRole().getRoleId().equals(AppConstant.ID_WAREK_3) ||
                user.getRole().getRoleId().equals(AppConstant.ID_WAREK_4) ||
                user.getRole().getRoleId().equals(AppConstant.ID_KEPEGAWAIAN_PUSAT)) {
            result = absensiDao.kehadiranPegawaiAll(tanggal, page);
            totalData = absensiDao.countAllByTanggalAndWaktuInIsNotNull(tanggal);
        } else {
            result = absensiDao.kehadiranPegawaiUnitkerja(tanggal, user.getPegawai().getUnitKerjaPresensi(), page);
            totalData = absensiDao.countAllByTanggalAndPegawaiUnitKerjaPresensiAndWaktuInIsNotNull(tanggal, user.getPegawai().getUnitKerjaPresensi());
        }

        mm.addAttribute("tanggal", tanggal);
        mm.addAttribute("data", result);
        mm.addAttribute("totalData", totalData);

        return "presensi/kehadiran";
    }

    @GetMapping("/pengajuan")
    public String showPengajuan(@RequestParam(required = false) String id, ModelMap mm, Authentication
            auth, @PageableDefault(size = 10) Pageable page) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Page<Pengajuan> datas = pengajuanDao.findAllByPegawaiAndTahunOrderByTanggalDesc(pegawai, LocalDate.now().getYear(), page);

        Pengajuan pengajuan = new Pengajuan();
        if (StringUtils.hasText(id)) {
            Optional<Pengajuan> o = pengajuanDao.findByPegawaiAndId(pegawai, id);
            if (o.isPresent()) {
                pengajuan = o.get();
            }
        }
        mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
        mm.addAttribute("pengajuan", pengajuan);
        mm.addAttribute("data", datas);

        return "presensi/pengajuan";
    }

    @PostMapping("/pengajuan")
    public String updatePengajuan(@ModelAttribute @Valid Pengajuan o, BindingResult
            errors, @RequestParam(value = "berkas") MultipartFile berkas,
                                  ModelMap mm, Authentication auth) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Page<Pengajuan> datas = pengajuanDao.findAllByPegawaiAndTahunOrderByTanggalDesc(pegawai, LocalDate.now().getYear(), null);
        mm.addAttribute("data", datas);

        log.info("error : {}", errors);

        if (o.getTanggalMulai() == null || o.getTanggalMulai().isAfter(o.getTanggalSelesai())) {
            errors.rejectValue("tanggalMulai", "tanggalMulai", "Periode mulai tidak boleh kosong atau tidak valid");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
        }

        if (o.getTanggalSelesai() == null || o.getTanggalSelesai().isBefore(o.getTanggalMulai())) {
            errors.rejectValue("tanggalSelesai", "tanggalSelesai", "Periode selesai tidak boleh kosong atau tidak valid");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
        }

        if (o.getStatusPresensi() == null
                || o.getStatusPresensi().getId().equals(AppConstant.STATUS_HADIR.getId())
                || o.getStatusPresensi().getId().equals(AppConstant.STATUS_ALFA.getId())) {
            errors.rejectValue("statusPresensi", "statusPresensi", "Status presensi tidak boleh kosong");
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
        }

        if (o.getIsUrl()) {
            if (o.getUrlShare().isEmpty() || o.getUrlShare().length() > 250) {
                // errors.rejectValue("urlShare", "urlShare", "URL tidak boleh kosong atau URL melebihi 250 karakter");
                mm.addAttribute("messageErrUrlShare", true);
                mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
            }
        } else {
            if (berkas.isEmpty() || berkas.getSize() > 614400) {
                errors.rejectValue("urlBukti", "urlBukti", "File Bukti tidak boleh kosong atau File terlalu besar (maks. 600 KB)");
                mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());
            }
        }
        //LOGGER.info("data: {}", o);
        if (errors.hasErrors()) {
            mm.addAttribute("listStatusPresensi", statusPresensiDao.findAllByFilter());

            return "presensi/pengajuan";
        } else {
            LocalDateTime tanggal = LocalDateTime.now();
            String url = "";

            Pengajuan pengajuan = new Pengajuan();
            BeanUtils.copyProperties(o, pengajuan, "id");

            pengajuan.setPegawai(pegawai);
            pengajuan.setTanggal(tanggal);
            pengajuan.setStatusVerifikasi(AppConstant.STATUS_DIAJUKAN);

            log.info("pengajuan: {}", pengajuan);
            pengajuanDao.save(pengajuan);

            if (!o.getIsUrl()) {
                url = filePengajuanService.uploadFile(berkas, pengajuan);
                pengajuan.setUrlBukti(url);
                pengajuanDao.save(pengajuan);
            }

            return "redirect:/presensi/pengajuan";
        }
    }

//    @GetMapping("/pengajuan/berkas/{id}")
//    public ResponseEntity<ByteArrayResource> downloadBerkasPengajuan(@PathVariable("id") String id) throws IOException {
//        Pengajuan pengajuan = pengajuanDao.getOne(id);
//        String file = pengajuan.getUrlBukti();
//
//        log.info("pengajuan: {}", pengajuan);
//
//        byte[] data = fileStorageService.getFile(file);
//        ByteArrayResource resource = new ByteArrayResource(data);
//
//        return ResponseEntity
//                .ok()
//                .contentLength(data.length)
//                .header("Content-type", "application/octet-stream")
//                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
//                .body(resource);
//
//    }

    @GetMapping("/pengaduan")
    public String showPengaduan(@RequestParam(required = false) String id, ModelMap mm, Authentication
            auth, @PageableDefault(size = 10) Pageable page) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Page<Pengaduan> datas = pengaduanDao.findAllByPegawaiAndTahunOrderByTanggalDesc(pegawai, LocalDate.now().getYear(), page);

        Pengaduan pengaduan = new Pengaduan();
        if (StringUtils.hasText(id)) {
            Optional<Pengaduan> o = pengaduanDao.findByPegawaiAndId(pegawai, id);
            if (o.isPresent()) {
                pengaduan = o.get();
            }
        }
        //mm.addAttribute("listStatusPresensi", statusPresensiDao.findAll());
        mm.addAttribute("pengaduan", pengaduan);
        mm.addAttribute("data", datas);

        return "presensi/pengaduan";
    }

    @PostMapping("/pengaduan")
    public String updatePengaduan(@ModelAttribute @Valid Pengaduan o, BindingResult
            errors, @RequestParam(value = "berkas") MultipartFile berkas, ModelMap mm, Authentication auth) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        Page<Pengaduan> datas = pengaduanDao.findAllByPegawaiAndTahunOrderByTanggalDesc(pegawai, LocalDate.now().getYear(), null);
        mm.addAttribute("data", datas);

        log.info("error : {}", errors);

        if (o.getTanggalMulai() == null || o.getTanggalMulai().isAfter(o.getTanggalSelesai())) {
            errors.rejectValue("tanggalMulai", "tanggalMulai", "Periode mulai tidak boleh kosong atau tidak valid");
        }

        if (o.getTanggalSelesai() == null || o.getTanggalSelesai().isBefore(o.getTanggalMulai())) {
            errors.rejectValue("tanggalSelesai", "tanggalSelesai", "Periode selesai tidak boleh kosong atau tidak valid");
        }

        if (berkas.isEmpty() || berkas.getSize() > 614400) {
            errors.rejectValue("urlBukti", "urlBukti", "File Bukti tidak boleh kosong atau File terlalu besar (maks. 600 KB)");
        }

        //LOGGER.info("data: {}", o);
        if (errors.hasErrors()) {

            return "presensi/pengaduan";
        } else {
            LocalDateTime tanggal = LocalDateTime.now();
            String url = "";
            try {
                url = fileStorageService.uploadFile(Objects.requireNonNull(berkas.getOriginalFilename()).toLowerCase().replace(" ", "_"), berkas, AppConstant.FOLDER_PENGADUAN);
            } catch (IOException e) {
                log.error("upload file error: " + e);
            }

            if (url.length() > 250) {
                errors.rejectValue("urlBukti", "urlBukti", "Nama file terlalu panjang");
            }

            Pengaduan pengaduan = new Pengaduan();
            BeanUtils.copyProperties(o, pengaduan);

            pengaduan.setPegawai(pegawai);
            pengaduan.setTanggal(tanggal);
            pengaduan.setStatusVerifikasi(AppConstant.STATUS_DIAJUKAN);
            pengaduan.setUrlBukti(url);

            log.info("pengaduan: {}", pengaduan);
            pengaduanDao.save(pengaduan);

            return "redirect:/home";
        }
    }

    @GetMapping("/pengaduan/berkas/{id}")
    public ResponseEntity<ByteArrayResource> downloadBerkasPengaduan(HttpServletRequest
                                                                             request, HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        Pengaduan pengaduan = pengaduanDao.getOne(id);
        String file = pengaduan.getUrlBukti();

        log.info("pengaduan: {}", pengaduan);

        byte[] data = fileStorageService.getFile(file);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource);

    }

    @GetMapping("/rekap")
    public String rekapGet(ModelMap mm, Authentication auth) {
//        Pegawai pegawai = currentService.getCurrentPegawai(auth);
//        LocalDate tanggal = LocalDate.now();
//
//        List<Absensi> datas = absensiDao.findAllAbsensiPegawaiTahunBulan(pegawai, tanggal.getYear(), tanggal.getMonthValue());
//        LOGGER.info("datas: {}", datas);
//        mm.addAttribute("data", datas);

        return "presensi/rekap";
    }

    @GetMapping("/in-out")
    public String getInOut(ModelMap mm, Authentication auth,
                           @RequestParam(value = "mulai", required = false) String mulai,
                           @RequestParam(value = "selesai", required = false) String selesai) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<InOutDto> result;

        // LOGGER.info("mulai: {}, selesai: {}", tglMulai, tglSelesai);
        result = hariKerjaDao.findAllByPeriode(pegawai, tglMulai, tglSelesai);

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", result);

        return "presensi/in-out";
    }

    @PostMapping("/in-out-print")
    public String printInOut(ModelMap mm, Authentication auth,
                             @RequestParam(value = "mulai", required = false) String mulai,
                             @RequestParam(value = "selesai", required = false) String selesai) {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawai);

        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<InOutDto> result;

        // LOGGER.info("mulai: {}, selesai: {}, unit: {}", tglMulai, tglSelesai, adminUnit);
        result = hariKerjaDao.findAllByPeriode(pegawai, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawai));
        mm.addAttribute("pegawai", pegawai);
        mm.addAttribute("data", result);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pejabat", pejabat);

        return "presensi/in-out-print";
    }

    @GetMapping("/rekap-pegawai")
    public String getRekapPegawai(ModelMap mm, Authentication auth,
                                  @RequestParam(value = "mulai", required = false) String mulai,
                                  @RequestParam(value = "selesai", required = false) String selesai) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> datas;
        if (pegawaiUtils.isPegawaiShift(pegawai)) {
            datas = absensiUtils.getRekapPresensiPegawaiShift(pegawai, tglMulai, tglSelesai);
        } else
            datas = absensiUtils.getRekapPresensiPegawai(pegawai, tglMulai, tglSelesai);

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", datas);
        mm.addAttribute("currentService", currentService);

        return "presensi/rekap-pegawai";
    }

    @PostMapping("/rekap-pegawai-print")
    public String printRekapPegawai(ModelMap mm, Authentication auth,
                                    @RequestParam(value = "mulai", required = false) String mulai,
                                    @RequestParam(value = "selesai", required = false) String selesai) {
        // LOGGER.info("mulai: {}, selesai: {}", mulai, selesai);
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawai);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> datas;
        if (pegawaiUtils.isPegawaiShift(pegawai)) {
            datas = absensiUtils.getRekapPresensiPegawaiShift(pegawai, tglMulai, tglSelesai);
        } else
            datas = absensiUtils.getRekapPresensiPegawai(pegawai, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawai));
        mm.addAttribute("pegawai", pegawai);
        mm.addAttribute("data", datas);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("currentService", currentService);

        return "presensi/rekap-pegawai-print";
    }

    @GetMapping("/rekap-dosen")
    public String getRekapDosen(ModelMap mm, Authentication auth,
                                @RequestParam(value = "mulai", required = false) String mulai,
                                @RequestParam(value = "selesai", required = false) String selesai) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> datas = absensiUtils.getRekapPresensiDosen(pegawai, tglMulai, tglSelesai);

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("data", datas);
        mm.addAttribute("currentService", currentService);

        return "presensi/rekap-dosen";
    }

    @PostMapping("/rekap-dosen-print")
    public String printRekapDosen(ModelMap mm, Authentication auth,
                                  @RequestParam(value = "mulai", required = false) String mulai,
                                  @RequestParam(value = "selesai", required = false) String selesai) {
        // LOGGER.info("mulai: {}, selesai: {}", mulai, selesai);
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        AdminUnit adminUnit = currentService.getUnitForTtd(pegawai);
        LocalDate tglMulai = mulai == null ? LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1) : LocalDate.parse(mulai);
        LocalDate tglSelesai = selesai == null ? LocalDate.now() : LocalDate.parse(selesai);

        List<RekapPresensiPegawaiDto> datas = absensiUtils.getRekapPresensiDosen(pegawai, tglMulai, tglSelesai);

        String pejabat = Pegawai.toStringNama(adminUnit == null ? new Pegawai() : adminUnit.getPjPegawai());

        mm.addAttribute("mulai", tglMulai);
        mm.addAttribute("selesai", tglSelesai);
        mm.addAttribute("nama", Pegawai.toStringNama(pegawai));
        mm.addAttribute("pegawai", pegawai);
        mm.addAttribute("data", datas);
        mm.addAttribute("unit", adminUnit);
        mm.addAttribute("pejabat", pejabat);
        mm.addAttribute("currentService", currentService);

        return "presensi/rekap-dosen-print";
    }

    @GetMapping("/change-password")
    public String getChangePasswordPage(@ModelAttribute UserDto o, ModelMap mm) {

        mm.addAttribute("data", new UserDto());

        return "presensi/change-password";
    }

    @PostMapping("/change-password")
    public String setChangePassword(@ModelAttribute UserDto o, RedirectAttributes ra, Authentication auth) {

        if (o.getUserPassword() == null || o.getUserPassword().isEmpty()) {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Password saat ini tidak boleh kosong");
            return "redirect:/presensi/change-password";
        }

        if (o.getNewPassword() == null || o.getNewPassword().isEmpty()) {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Password baru tidak boleh kosong");
            return "redirect:/presensi/change-password";
        }

        if (o.getPasswordConfirm() == null || o.getPasswordConfirm().isEmpty()) {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Password konfirmasi tidak boleh kosong");
            return "redirect:/presensi/change-password";
        }

        if (!o.getNewPassword().equals(o.getPasswordConfirm())) {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Password baru dan password konfirmasi tidak cocok");
            return "redirect:/presensi/change-password";
        }

        User user = currentService.getCurrentUser(auth);
        if (!passwordEncoder.encode(o.getUserPassword()).equals(user.getUserPassword())) {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Password saat ini tidak sesuai");
            return "redirect:/presensi/change-password";
        }

        user.setUserPassword(passwordEncoder.encode(o.getNewPassword()));
        userService.save(user);

        return "redirect:/home";
    }
}
