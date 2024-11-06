package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.FotoProfileDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.PegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.FotoProfileDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FotoProfile;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Slf4j
public class FotoProfileService {
    public final static int MIN_FOTO_SIZE = 102400;
    public final static int MAX_FOTO_SIZE = 819200;
    public final static String FOLDER_STAFF = "staff";
    public final static String PROFILE = "profile";
    public final static String IMG_EXT = "jpg";
    private final static String ROUTE_FILE_PROFILE = "/file-profile/";

    @Autowired
    private FotoProfileDao fotoProfileDao;

    @Autowired
    private PegawaiDao pegawaiDao;

    @Value("${file.storage.multimedia.path}")
    String uploadDirectoryMultimedia;

    @Value("${app.default.url.photo}")
    private String defaultUrlPhoto;

    public Optional<FotoProfile> getFotoProfileByPegawai(Pegawai pegawai) {

        return fotoProfileDao.findByPegawai(pegawai);
    }

    public Boolean isPassTargetDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2021);
        cal.set(Calendar.MONTH, 11); //Desember
        cal.set(Calendar.DATE, 19);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        Date tanggalBatas = cal.getTime();
        return new Date().after(tanggalBatas);
    }

//    public String uploadFileFoto(MultipartFile image, Pegawai pegawai) {
//
//        String url = "";
//        try {
//            String name = pegawai.getNik() + "_" + PROFILE + "." + IMG_EXT;
//            url = uploadFoto(name.toLowerCase().replace(" ", "_"), image, pegawai.getNik());
//        } catch (IOException e) {
//            log.error("bucket errornya: ", e);
//        }
//
//        return url;
//    }

    public String getViewableUrl(FotoProfile fotoProfile, String urlBerkas) {
        String objectName;
        if (urlBerkas.isEmpty()) {
            objectName = ROUTE_FILE_PROFILE + fotoProfile.getUrlBukti();
        } else
            objectName = urlBerkas;

        return objectName;
    }

    public String getDefaultFotoUrl(Optional<FotoProfile> fotoProfile) {

        String url;
        if (fotoProfile.isPresent() && fotoProfile.get().getStatus())
            url = getViewableUrl(fotoProfile.get(), "");
        else
            url = defaultUrlPhoto;

        return url;
    }

    public void saveFotoProfile(FotoProfile fotoProfile) {
        fotoProfileDao.save(fotoProfile);
    }

    public Boolean isJPEG(InputStream inputStream) throws IOException {
        try (DataInputStream ins = new DataInputStream(inputStream)) {
            return ins.readInt() == 0xffd8ffe0;
        }
    }

    public Boolean isJPEG(MultipartFile image) {
        return Objects.equals(image.getContentType(), "image/jpg") || Objects.equals(image.getContentType(), "image/jpeg");
    }

    public byte[] getFile(String file) throws IOException {
        Path imagePath = Paths.get(file);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            return null; // Handle missing images
        }
    }

//    public void removeFileFoto(FotoProfile fotoProfile) throws IOException {
//        removeFile(file);
//    }

    private String uploadFoto(String name, MultipartFile file, String info) throws IOException {
        String folder = uploadDirectoryMultimedia + "/" + FotoProfileService.FOLDER_STAFF + "/" + info + "/" + FotoProfileService.PROFILE;

        Path uploadPath = Paths.get(folder);
        Path filePath = uploadPath.resolve(name);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private void removeFile(String file) throws IOException {
        Path imagePath = Paths.get(file);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
    }

    public Page<FotoProfileDto> getAllByPegawaiAndStatusOrderByCreatedAtDesc(Pegawai pegawai, Boolean status, Pageable page) {

        List<FotoProfile> fotoProfilePage = fotoProfileDao.findAllByPegawaiAndStatusOrderByCreatedAtDesc(pegawai, status, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = fotoProfileDao.countAllByPegawaiAndStatusOrderByCreatedAtDesc(pegawai, status);

        for (FotoProfile oFotoProfile : fotoProfilePage) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oFotoProfile));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Long idPegawai, Boolean status, Pageable page) {

        List<FotoProfile> fotoProfilePage = fotoProfileDao.findAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, idPegawai, status, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = fotoProfileDao.countAllByPegawaiJenisPegawaiKategoriPegawaiIdAndPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, idPegawai, status);

        for (FotoProfile oFotoProfile : fotoProfilePage) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oFotoProfile));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(Long idKategoriPegawai, Boolean status, Pageable page) {

        List<FotoProfile> fotoProfilePage = fotoProfileDao.findAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, status, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = fotoProfileDao.countAllByPegawaiJenisPegawaiKategoriPegawaiIdAndStatusOrderByCreatedAtDesc(idKategoriPegawai, status);

        for (FotoProfile oFotoProfile : fotoProfilePage) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oFotoProfile));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllByStatusOrderByCreatedAtDesc(Boolean status, Pageable page) {

        List<FotoProfile> fotoProfilePage = fotoProfileDao.findAllByStatusOrderByCreatedAtDesc(status, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = fotoProfileDao.countAllByStatusOrderByCreatedAtDesc(status);

        for (FotoProfile oFotoProfile : fotoProfilePage) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oFotoProfile));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllPegawaiBelumUploadByPegawaiOrderByCreatedAtDesc(Pegawai pegawai, Pageable page) {

        List<Pegawai> pegawaiList = pegawaiDao.findAllPegawaiBelumUploadFotoByPegawaiOrderByName(pegawai, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = pegawaiDao.countAllPegawaiBelumUploadFotoByPegawaiOrderByName(pegawai);

        for (Pegawai oPegawai : pegawaiList) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oPegawai));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllPegawaiBelumUploadByKategoriPegawaiAndPegawaiIdOrderByCreatedAtDesc(Long idKategoriPegawai, Long idPegawai, Pageable page) {

        List<Pegawai> pegawaiList = pegawaiDao.findAllPegawaiBelumUploadFotoByKategoriPegawaiIdAndPegawaiIdOrderByName(idKategoriPegawai, idPegawai, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = pegawaiDao.countAllPegawaiBelumUploadFotoByKategoriPegawaiIdAndPegawaiIdOrderByName(idKategoriPegawai, idPegawai);

        for (Pegawai oPegawai : pegawaiList) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oPegawai));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllPegawaiBelumUploadByKategoriPegawaiAndOrderByCreatedAtDesc(Long idKategoriPegawai, Pageable page) {

        List<Pegawai> pegawaiList = pegawaiDao.findAllPegawaiBelumUploadFotoByKategoriPegawaiIdOrderByName(idKategoriPegawai, page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = pegawaiDao.countAllPegawaiBelumUploadFotoByKategoriPegawaiIdOrderByName(idKategoriPegawai);

        for (Pegawai oPegawai : pegawaiList) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oPegawai));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    public Page<FotoProfileDto> getAllPegawaiBelumUploadOrderByCreatedAtDesc(Pageable page) {

        List<Pegawai> pegawaiList = pegawaiDao.findAllPegawaiBelumUploadFotoOrderByName(page);
        List<FotoProfileDto> fotoProfileDtoList = new LinkedList<>();
        int sizeAllContent = pegawaiDao.countAllPegawaiBelumUploadFotoOrderByName();

        for (Pegawai oPegawai : pegawaiList) {
            fotoProfileDtoList.add(setDataFotoProfileDto(oPegawai));
        }

        int[] startEnd = getStartAndEndFromList(fotoProfileDtoList, page);
        return new PageImpl<>(fotoProfileDtoList.subList(startEnd[0], startEnd[1]), page, sizeAllContent);
    }

    private FotoProfileDto setDataFotoProfileDto(FotoProfile fotoProfile) {
        FotoProfileDto fotoProfileDto = new FotoProfileDto();

        fotoProfileDto.setId(fotoProfile.getId());
        try {
            fotoProfileDto.setUrlBerkas(getViewableUrl(fotoProfile, ""));
        } catch (Exception e) {
            log.error("Error resolve url berkas {}}", e.toString());
        }
        fotoProfileDto.setNama(Pegawai.toStringNama(fotoProfile.getPegawai()));
        fotoProfileDto.setIdPegawai(fotoProfile.getPegawai().getId());
        fotoProfileDto.setJenisKelamin(fotoProfile.getPegawai().getJenisKelamin());
        fotoProfileDto.setKategoriPegawai(fotoProfile.getPegawai().getJenisPegawai() == null ? ""
                : fotoProfile.getPegawai().getJenisPegawai().getKategoriPegawai().getNama());
        fotoProfileDto.setNik(fotoProfile.getPegawai().getNik());
        fotoProfileDto.setUnit(fotoProfile.getPegawai().getUnitKerjaPresensi() == null ? "" : fotoProfile.getPegawai().getUnitKerjaPresensi().getNama());
        fotoProfileDto.setUnitKerjaPresensi(fotoProfile.getPegawai().getUnitKerjaPresensi() == null ? ""
                : (fotoProfile.getPegawai().getUnitKerjaPresensi().getUnit() == null ? "" : fotoProfile.getPegawai().getUnitKerjaPresensi().getUnit().getNama()));
        fotoProfileDto.setNip(fotoProfile.getPegawai().getNip());
        fotoProfileDto.setStatus(fotoProfile.getStatus());

        return fotoProfileDto;
    }

    private static FotoProfileDto setDataFotoProfileDto(Pegawai pegawai) {
        FotoProfileDto fotoProfileDto = new FotoProfileDto();

        fotoProfileDto.setId(null);
        fotoProfileDto.setUrlBerkas(null);
        fotoProfileDto.setNama(Pegawai.toStringNama(pegawai));
        fotoProfileDto.setIdPegawai(pegawai.getId());
        fotoProfileDto.setJenisKelamin(pegawai.getJenisKelamin());
        fotoProfileDto.setKategoriPegawai(pegawai.getJenisPegawai().getKategoriPegawai().getNama());
        fotoProfileDto.setNik(pegawai.getNik());
        fotoProfileDto.setUnit(pegawai.getUnitKerjaPresensi() == null ? "" : pegawai.getUnitKerjaPresensi().getNama());
        fotoProfileDto.setUnitKerjaPresensi(pegawai.getUnitKerjaPresensi() == null ? ""
                : (pegawai.getUnitKerjaPresensi().getUnit() == null ? "" : pegawai.getUnitKerjaPresensi().getUnit().getNama()));
        fotoProfileDto.setNip(pegawai.getNip());
        fotoProfileDto.setStatus(null);

        return fotoProfileDto;
    }

    private static int[] getStartAndEndFromList(List<FotoProfileDto> lists, Pageable page) {
        //final int start = (int) page.getOffset();
        final int start = 0;
        final int end = Math.min((start + page.getPageSize()), lists.size());

        return new int[]{start, end};
    }
}
