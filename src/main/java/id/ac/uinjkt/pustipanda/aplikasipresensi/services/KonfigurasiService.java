package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.KonfigurasiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Konfigurasi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KonfigurasiService {

    @Autowired
    private KonfigurasiDao konfigurasiDao;

    public List<Konfigurasi> getListByNamaAndNilai(String nama, String nilai) {
        return konfigurasiDao.findByNamaAndNilai(nama, nilai);
    }

    public Konfigurasi getKonfigurasi(String nama) {
        Optional<Konfigurasi> konfigurasi = konfigurasiDao.findFirstByNamaOrderByIdDesc(nama);

        if (!konfigurasi.isPresent()) {
            konfigurasi = Optional.of(new Konfigurasi());
            konfigurasi.get().setNama(AppConstant.ABSEN_BY_AREA);
            konfigurasi.get().setNilai(AppConstant.TIDAK_AKTIF);

            save(konfigurasi.get());
        }

        return konfigurasi.get();
    }

    public void save(Konfigurasi konfigurasi){
        konfigurasiDao.save(konfigurasi);
    }
}
