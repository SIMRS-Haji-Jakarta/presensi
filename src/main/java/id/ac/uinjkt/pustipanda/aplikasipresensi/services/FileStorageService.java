package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FileStorageService {
    @Value("${file.storage.presensi.path}")
    String uploadDirectoryPresensi;

    public String uploadFile(String name, MultipartFile imageFile, String tipe) throws IOException {
        LocalDateTime tanggal = LocalDateTime.now();

        String folder = uploadDirectoryPresensi + "/" + tipe + "/" + tanggal.getYear() + "/" + tanggal.getMonthValue() + "/" + tanggal.getDayOfMonth();

        Path uploadPath = Paths.get(folder);
        Path filePath = uploadPath.resolve(name);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    // To view an image
    public byte[] getFile(String fileUrl) throws IOException {
        Path imagePath = Paths.get(fileUrl);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            return null; // Handle missing images
        }
    }

    // Delete an image
    public void deleteFile(String fileUrl) throws IOException {

        Path imagePath = Paths.get(fileUrl);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
    }

    public String getViewableUrlBukti(String objectName) {
        String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return homeURL + '/' + objectName;
    }
}
