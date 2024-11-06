package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FilePengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FileProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FilePengajuanService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.FileProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@Slf4j
public class FileController {
    @Autowired
    private FileProfileService fileProfileService;

    @Autowired
    private FilePengajuanService filePengajuanService;

    @GetMapping(value = "/file-profile/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable String id) {
        Optional<FileProfile> fileProfile = fileProfileService.findById(id);
        if (fileProfile.isPresent()) {
            log.info("file profile: {}", fileProfile.get().getId());

            byte[] data = fileProfile.get().getData();
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileProfile.get().getNama() + "\"")
                    .body(resource);
        }
        return null;
    }

    @GetMapping(value = "/file-pengajuan/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String id, @RequestParam(value = "type", required = false) String type) {
        if (type == null || type.isEmpty())
            type = "inline";

        Optional<FilePengajuan> filePengajuan = filePengajuanService.findById(id);
        if (filePengajuan.isPresent()) {
            log.info("file pengajuan: {}", filePengajuan.get().getId());

            byte[] data = filePengajuan.get().getData();
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    //.header("Content-type", "application/octet-stream")
                    .header("Content-disposition", type + "; filename=\"" + filePengajuan.get().getNama() + "\"")
                    .contentType(MediaType.parseMediaType(filePengajuan.get().getType()))
                    .body(resource);
        }
        return null;
    }
}
