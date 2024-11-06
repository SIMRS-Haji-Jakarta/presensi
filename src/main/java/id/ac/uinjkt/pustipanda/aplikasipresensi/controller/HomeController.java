package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.CurrentService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileProfileService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FotoProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class HomeController {
    @Autowired
    private CurrentService currentService;

    //    @Value("${app.api.gambar}")
//    private String urlApiGambar;

    @Autowired
    private FotoProfileService fotoProfileService;

    @Autowired
    private FileProfileService fileProfileService;

    @RequestMapping(value = {"/home"})
    public String home(ModelMap mm, Authentication auth, HttpServletRequest request, RedirectAttributes ra) {
        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        // log.info("pegawai: {}", pegawai);

        if (pegawai == null) {
            ra.addFlashAttribute("error", "Pegawai tidak ditemukan");

            return "redirect:/login";
        }

        Optional<FotoProfile> fotoProfile = fotoProfileService.getFotoProfileByPegawai(pegawai);
        String url = fotoProfileService.getDefaultFotoUrl(fotoProfile);
        if (!fotoProfile.isPresent()) {
            //Aktifkan tombol upload foto id card ketika foto profile status false
            mm.addAttribute("fotoProfile", new FotoProfile());
            mm.addAttribute("triggerUploadFoto", false);
            //Panggil trigger di tanggal 13 Desember 2021
            if (fotoProfileService.isPassTargetDate())
                mm.addAttribute("triggerUploadFoto", true);
        } else {
            fotoProfile.get().setUrlBukti(fotoProfileService.getViewableUrl(fotoProfile.get(), ""));
            mm.addAttribute("triggerUploadFoto", false);
            mm.addAttribute("fotoProfile", fotoProfile.get());
        }

        //untuk trigger cek no absen
        List<String> listErrNoAbsen = currentService.getListErrorNoAbsen(pegawai);
        if (listErrNoAbsen.size() > 0) {
            mm.addAttribute("triggerUpdateNoAbsen", true);
            mm.addAttribute("message", listErrNoAbsen.stream().map(String::toString)
                    .collect(Collectors.joining("<br />")));
        } else
            mm.addAttribute("triggerUpdateNoAbsen", false);

        mm.addAttribute("nama", Pegawai.toStringNama(pegawai));
        mm.addAttribute("url", url);
        mm.addAttribute("pegawai", pegawai);

        return "home";
    }

    @PostMapping(value = {"/home/foto/form"})
    public String postFotoProfile(Authentication auth, RedirectAttributes ra, @RequestParam("image") MultipartFile image) throws IOException {

        Pegawai pegawai = currentService.getCurrentPegawai(auth);
        if (pegawai != null) {
            Optional<FotoProfile> fotoProfile = fotoProfileService.getFotoProfileByPegawai(pegawai);
            if (!fotoProfile.isPresent()) {
                fotoProfile = Optional.of(new FotoProfile());
                fotoProfile.get().setPegawai(pegawai);

                fotoProfileService.saveFotoProfile(fotoProfile.get());
            }

            if (image.isEmpty() || !fotoProfileService.isJPEG(image)
                    || image.getSize() < FotoProfileService.MIN_FOTO_SIZE || image.getSize() > FotoProfileService.MAX_FOTO_SIZE) {
                ra.addFlashAttribute("messageErrImage", true);
                ra.addFlashAttribute("messageErrSave", true);

                return "redirect:/home#modal-upload-foto";
            }

            //cek apakah bisa upload foto ulang
            if (fotoProfile.get().getStatus()) {
                return "redirect:/home";
            } else {
                //hapus foto sebelumnya jika sudah ada
                if (fotoProfile.get().getUrlBukti() != null)
                    fileProfileService.removeFile(fotoProfile.get());
            }

            //String url = fotoProfileService.uploadFileFoto(image, pegawai);
            String idFileProfile = fileProfileService.uploadFile(image, fotoProfile.get());
            fotoProfile.get().setUrlBukti(idFileProfile);

            try {
                //pastikan url foto tidak kosong
                if (fotoProfile.get().getUrlBukti() != null) {
                    fotoProfileService.saveFotoProfile(fotoProfile.get());
                    ra.addFlashAttribute("messageSuccess", true);
                    log.info("Sukses save foto profile: {}", fotoProfile.get().getPegawai().getId());
                } else {
                    log.error("foto profile tidak memiliki file profile");
                    ra.addFlashAttribute("messageErrSave", true);

                    return "redirect:/home#modal-upload-foto";
                }

            } catch (Exception e) {
                log.error("Error save foto profile, error nya: ", e);
                ra.addFlashAttribute("messageErrSave", true);

                return "redirect:/home#modal-upload-foto";
            }

        }

        return "redirect:/home";
    }
}
