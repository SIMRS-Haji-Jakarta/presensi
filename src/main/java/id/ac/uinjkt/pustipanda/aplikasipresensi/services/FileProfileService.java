package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.FileProfileDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FileProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class FileProfileService {

    @Autowired
    private FileProfileDao fileProfileDao;

    public final static String PROFILE = "profile";
    public final static String IMG_EXT = "jpg";

    public String uploadFile(MultipartFile image, FotoProfile fotoProfile) {

        String url = null;
        try {
            url = saveFileProfile(fotoProfile, image, image.getContentType());
        } catch (IOException e) {
            log.error("file errornya: ", e);
        }

        return url;
    }

    private String saveFileProfile(FotoProfile fotoProfile, MultipartFile file, String type) throws IOException {

        Optional<FileProfile> fileProfile = fileProfileDao.findByFotoProfile(fotoProfile);
        if (!fileProfile.isPresent()) {
            fileProfile = Optional.of(new FileProfile());
            fileProfile.get().setFotoProfile(fotoProfile);
        }

        if (file.isEmpty()) {
            return null;
        }

        byte[] imgByte = file.getBytes();
        String name = fotoProfile.getPegawai().getNik() + "_" + PROFILE + "." + IMG_EXT;

        fileProfile.get().setData(imgByte);
        fileProfile.get().setNama(name);
        fileProfile.get().setType(type);

        save(fileProfile.get());

        return fileProfile.get().getId();
    }

//    public String getViewableUrl(FotoProfile fotoProfile) {
//        Optional<FileProfile> fileProfile = fileProfileDao.findByFotoProfile(fotoProfile);
////        if (fileProfile.isPresent()) {
////            //byte[] decodeByte = Base64.getDecoder().decode(fileProfile.get().getData());
////            return Base64.nco(fileProfile.get().getData());
////        }
////
////        return null;
//        return fileProfile.map(profile -> new String(profile.getData())).orElse(null);
//    }

    private void save(FileProfile fileProfile) {
        fileProfileDao.save(fileProfile);
    }

    public Optional<FileProfile> findById(String id) {
        return fileProfileDao.findById(id);
    }

    public void removeFile(FotoProfile fotoProfile) {
        Optional<FileProfile> data = fileProfileDao.findByFotoProfile(fotoProfile);
        if (data.isPresent())
            fileProfileDao.delete(data.get());
    }
}
