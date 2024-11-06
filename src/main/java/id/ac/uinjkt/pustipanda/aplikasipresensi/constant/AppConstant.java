package id.ac.uinjkt.pustipanda.aplikasipresensi.constant;

import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.*;

public interface AppConstant {
    public static final String REDIRECT = "redirect:";
    public static final String MESSAGE = "message";
    final StatusPresensi STATUS_HADIR = new StatusPresensi("7999-1");
    final StatusPresensi STATUS_ALFA = new StatusPresensi("7999-6");
    final StatusPresensi STATUS_DINAS_LUAR = new StatusPresensi("7999-2");
    final StatusPresensi STATUS_CUTI = new StatusPresensi("7999-3");
    final StatusPresensi STATUS_IZIN = new StatusPresensi("7999-4");
    final StatusPresensi STATUS_SAKIT = new StatusPresensi("7999-5");
    final StatusPresensi STATUS_TUGAS_BELAJAR = new StatusPresensi("7999-7");
    final JamKerja JAM_NORMAL = new JamKerja("7997-1");
    final JamKerja JAM_NORMAL_JUMAT = new JamKerja("7997-2");
    final StatusHari HARI_MASUK = new StatusHari("7998-1");
    final StatusHari HARI_LIBUR = new StatusHari("7998-2");
    final String ABSEN_BY_AREA = "absen_by_area";
    final String AKTIF = "aktif";
    final String TIDAK_AKTIF = "tidak_aktif";
    final String TERLAMBAT = "terlambat";
    final String KECEPATAN = "kecepatan";
    final String TERLAMBAT_SHIFT = "terlambat_shift";
    final String KECEPATAN_SHIFT = "kecepatan_shift";

    final int UIN_JAKARTA_COMPANY = 1;
    final int PERSONNEL_POSITION_DOSEN = 1;
    final int PERSONNEL_POSITION_TENDIK = 2;
    final short TRANSACTION_PRESENSI_CHECK_IN = 0;
    final short TRANSACTION_PRESENSI_BREAK_IN = 3;
    final short TRANSACTION_PRESENSI_OVERTIME_IN = 4;
    final short TRANSACTION_PRESENSI_CHECK_OUT = 1;
    final short TRANSACTION_PRESENSI_BREAK_OUT = 2;
    final short TRANSACTION_PRESENSI_OVERTIME_OUT = 5;

    final StatusVerifikasi STATUS_DIAJUKAN = new StatusVerifikasi("7997-1");
    final StatusVerifikasi STATUS_SEDANG_DIPROSES = new StatusVerifikasi("7997-2");
    final StatusVerifikasi STATUS_DITERIMA = new StatusVerifikasi("7997-5");
    final StatusVerifikasi STATUS_DITOLAK = new StatusVerifikasi("7997-4");
    final StatusVerifikasi STATUS_DITERUSKAN = new StatusVerifikasi("7997-3");

    final Long KATEGORI_PNS = 1L;

    final String ID_REKTOR = "rektor";
    final String ID_WAREK_1 = "purek1";
    final String ID_WAREK_2 = "warek2";
    final String ID_WAREK_3 = "warek3";
    final String ID_WAREK_4 = "warek4";
    final String ID_KEPEGAWAIAN_PUSAT = "pegpst";

    final String BULAN[] = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"};

    final Double DEFAULT_LATITUDE = -6.306457;
    final Double DEFAULT_LONGITUDE = 106.755821;
    final String DEFAULT_IP = "172.27.18.74";
    final String FOLDER_PENGAJUAN = "pengajuan";
    final String FOLDER_PENGADUAN = "pengaduan";

    final StatusPresensi[] LIST_STATUS_PRESENSI = new StatusPresensi[]{STATUS_CUTI, STATUS_DINAS_LUAR, STATUS_TUGAS_BELAJAR};

    final JamKerjaShift JAM_KERJA_SHIFT_MALAM = new JamKerjaShift("8998-3");
    final JamKerjaShift JAM_KERJA_SHIFT_LIBUR = new JamKerjaShift("8998-4");

    String ATTLOG = "ATTLOG";
    String OPLOG = "OPERLOG";
    String ATTPHOTO = "ATTPHOTO";

    public static final String TEMPLATE_USER = "pusat/pengguna/list";
    public static final String LINK_USER = REDIRECT + "/pusat/pengguna/list";
    public static final String TEMPLATE_JABATAN = "pusat/jabatan/list";
    public static final String LINK_JABATAN = REDIRECT + "/pusat/jabatan/list";
    public static final String TEMPLATE_UNIT = "pusat/unit/list";
    public static final String LINK_UNIT = REDIRECT + "/pusat/unit/list";
    public static final String TEMPLATE_UNIT_KERJA = "pusat/unit-kerja/list";
    public static final String LINK_UNIT_KERJA = REDIRECT + "/pusat/unit-kerja/list";

    public static final String TEMPLATE_PEGAWAI = "pusat/pegawai/list";
    public static final String LINK_PEGAWAI = REDIRECT + "/pusat/pegawai/list";
}
