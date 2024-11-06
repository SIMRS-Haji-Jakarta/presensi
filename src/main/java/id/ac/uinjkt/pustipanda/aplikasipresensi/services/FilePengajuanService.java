package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.FilePengajuanDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FilePengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pengajuan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class FilePengajuanService {

    @Autowired
    private FilePengajuanDao filePengajuanDao;

    public String uploadFile(MultipartFile image, Pengajuan pengajuan) {

        String url = null;
        try {
            url = saveFilePengajuan(pengajuan, image, image.getContentType());
        } catch (IOException e) {
            log.error("file errornya: ", e);
        }

        return url;
    }

    private String saveFilePengajuan(Pengajuan pengajuan, MultipartFile file, String type) throws IOException {

        Optional<FilePengajuan> filePengajuan = filePengajuanDao.findByPengajuan(pengajuan);
        if (!filePengajuan.isPresent()) {
            filePengajuan = Optional.of(new FilePengajuan());
            filePengajuan.get().setPengajuan(pengajuan);
        }

        if (file.isEmpty()) {
            return null;
        }

        byte[] imgByte = file.getBytes();
        String name = file.getOriginalFilename();

        filePengajuan.get().setData(imgByte);
        filePengajuan.get().setNama(name);
        filePengajuan.get().setType(type);

        save(filePengajuan.get());

        return filePengajuan.get().getId();
    }

    private void save(FilePengajuan filePengajuan) {
        filePengajuanDao.save(filePengajuan);
    }

    public Optional<FilePengajuan> findById(String id) {
        return filePengajuanDao.findById(id);
    }

    public void removeFile(Pengajuan pengajuan) {
        Optional<FilePengajuan> data = filePengajuanDao.findByPengajuan(pengajuan);
        if (data.isPresent())
            filePengajuanDao.delete(data.get());
    }
}
