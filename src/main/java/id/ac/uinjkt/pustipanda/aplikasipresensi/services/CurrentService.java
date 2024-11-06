package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.AdminUnitDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.UserDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.AdminUnit;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.FilePengajuan;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CurrentService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminUnitDao adminUnitDao;

    @Autowired
    private PegawaiService pegawaiService;

    @Autowired
    private FilePengajuanService filePengajuanService;

    public User getCurrentUser(Authentication auth) {
        String u = auth.getName();
        User user = userDao.findByAktifTrueAndUserId(u);

        return user == null ? null : user;
    }

    public Pegawai getCurrentPegawai(Authentication auth) {
        String u = auth.getName();
        User user = userDao.findByAktifTrueAndUserId(u);

        return user.getPegawai() == null ? null : user.getPegawai();
    }

    public AdminUnit getCurrentAdminUnit(Authentication auth) {
        String u = auth.getName();
        User user = userDao.findByAktifTrueAndUserId(u);
        Pegawai pegawai = user.getPegawai() == null ? null : user.getPegawai();
        AdminUnit adminUnit = adminUnitDao.findByPegawai(pegawai);

        return adminUnit == null ? null : adminUnit;
    }

    public AdminUnit getUnitForTtd(Pegawai pegawai) {
        Optional<AdminUnit> oAdminUnit = adminUnitDao.findByUnitKerja(pegawai.getUnitKerjaPresensi());

        return oAdminUnit.isPresent() ? oAdminUnit.get() : null;
    }

    public AdminUnit getUnitForTtdByPegawaiAU(Pegawai pegawai) {
        Optional<AdminUnit> oAdminUnit = Optional.ofNullable(adminUnitDao.findByPegawai(pegawai));

        return oAdminUnit.isPresent() ? oAdminUnit.get() : null;
    }

    public Integer getExactMinuteNumber(Integer number) {
        if (number == null)
            number = 0;

        BigDecimal minute = BigDecimal.valueOf(number / 60);
        minute.setScale(0, RoundingMode.DOWN);

        return minute.intValue();
    }

    public Integer getExactHourNumber(Integer number) {
        if (number == null)
            number = 0;

        BigDecimal hour = BigDecimal.valueOf(number / 3600);
        hour.setScale(0, RoundingMode.DOWN);

        return hour.intValue();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getMimeType(boolean isUrl, String url) {
        if (isUrl) {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            return fileNameMap.getContentTypeFor(url);
        } else {
            Optional<FilePengajuan> filePengajuan = filePengajuanService.findById(url);
            if (filePengajuan.isPresent())
                return filePengajuan.get().getType();
        }
        return "";
    }

    public List<String> getListErrorNoAbsen(Pegawai pegawai) {
        List<String> err = new LinkedList<>();
        if (pegawai.getNomorAbsen() == null || pegawai.getNomorAbsen().isEmpty()) {
            err.add("Nomor Absen tidak boleh kosong, " +
                    "segera konfirmasikan hal ini ke Bagian Kepegawaian untuk mendapatkan Nomor Absen yang sesuai dengan ketentuan");
        }
        if (pegawai.getNomorAbsen() != null && !isNumeric(pegawai.getNomorAbsen())) {
            err.add("Nomor Absen harus berupa angka, " +
                    "segera konfirmasikan hal ini ke Bagian Kepegawaian untuk mendapatkan Nomor Absen yang sesuai dengan ketentuan");
        }

        if (pegawai.getNomorAbsen() != null && pegawaiService.isNomorAbsenDuplicate(pegawai)) {
            err.add("Nomor Absen duplikat dengan Nomor Absen lain, " +
                    "segera konfirmasikan duplikasi ini ke Bagian Kepegawaian untuk mendapatkan Nomor Absen yang sesuai dengan ketentuan");
        }

        return err;
    }

    public String getStringFromTime(Long number) {
        String result = "";
        if (number != null) {
            int num = Math.toIntExact(number);
            Integer jam = getExactHourNumber(num);
            Integer sisa = num % 3600;
            Integer menit = getExactMinuteNumber(sisa);
            Integer detik = num % 60;

            result = jam.toString() + ':' + menit.toString() + ':' + detik;
        }

        return result;
    }
}
