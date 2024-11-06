package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi.JenisPegawaiDao;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.JenisPegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class JenisPegawaiService {
    final Long[] JENIS_PEGAWAI_DOSEN = {1L, 3L};
    final Long[] JENIS_PEGAWAI_TENDIK = {2L, 4L, 5L, 6L, 8L};

    @Autowired
    private JenisPegawaiDao jenisPegawaiDao;

    public boolean isJenisPegawaiDosen(Pegawai pegawai) {
        return Arrays.stream(JENIS_PEGAWAI_DOSEN).findAny().filter(aLong -> pegawai.getJenisPegawai().getId().equals(aLong)).isPresent();
    }

    public List<JenisPegawai> findAll() {
        return jenisPegawaiDao.findAll();
    }
}
