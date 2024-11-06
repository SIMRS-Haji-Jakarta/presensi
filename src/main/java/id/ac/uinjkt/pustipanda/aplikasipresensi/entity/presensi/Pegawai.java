package id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.StringTokenizer;

@Entity
@Table(schema = "public", name = "pegawai")
@Data
public class Pegawai implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "nama")
    private String nama;

    @Column(name = "gelardepan")
    private String gelarDepan;

    @Column(name = "gelarbelakang")
    private String gelarBelakang;

    @Column(name = "kelamin", length = 20)
    private String jenisKelamin;

    @Column(name = "tanggallahir", columnDefinition = "DATE")
    private LocalDate tanggalLahir;

    @Column(name = "no_absen", length = 10)
    private String nomorAbsen;

    @Column(name = "is_face")
    private Boolean isFace;

    @Column(name = "mycode", length = 100)
    private String nip;

    @Column(name = "ktp", length = 50)
    private String nik;

    @Column(name = "spesialisasi1")
    private String spesialisasi1;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "jenis_pegawai")
    private JenisPegawai jenisPegawai;

    @ManyToOne
    @JoinColumn(name = "status_kepegawaian")
    private StatusPegawai statusPegawai;

    @ManyToOne
    @JoinColumn(name = "unit_kerja_presensi")
    private UnitKerjaPresensi unitKerjaPresensi;

    @ManyToOne
    @JoinColumn(name = "jabatan_fungsional")
    private JabatanPegawai jabatanPegawai;

    @ManyToOne
    @JoinColumn(name = "golongan_pegawai")
    private Golongan golongan;

    public static String toStringNama(Pegawai pegawai) {
        if (pegawai == null || pegawai.getId() == null)
            return "";

        String gelarDepan = pegawai.getGelarDepan() == null ? "" : pegawai.getGelarDepan();
        String nama = pegawai.getNama() == null ? "" : pegawai.getNama();
        String gelarBelakang = pegawai.getGelarBelakang() == null ? "" : pegawai.getGelarBelakang();

        String value = gelarDepan;
        value = (value.trim().equals("") ? "" : (value + " ")) + nama;
        value = (value.trim().equals("") ? "" : (value + " ")) + gelarBelakang;

        return value;
    }

    public static String toFirstName(Pegawai pegawai) {
        int maxLengthChar = 25;
        String firstTwoWords = null;

        // Use StringTokenizer to split the string into words
        StringTokenizer tokenizer = new StringTokenizer(pegawai.getNama());

        // Check if there are at least two words
        if (tokenizer.countTokens() > 0) {
            // Extract the first two words
            firstTwoWords = tokenizer.nextToken() + (tokenizer.hasMoreTokens() ? " " + tokenizer.nextToken() : "");
        }

        String gelarDepan = pegawai.getGelarDepan() == null ? "" : pegawai.getGelarDepan();
        String gelarBelakang = pegawai.getGelarBelakang() == null ? "" : pegawai.getGelarBelakang();

        String value = gelarDepan;
        String value2 = null;

        if (firstTwoWords != null && firstTwoWords.length() <= maxLengthChar)
            value = getValidGelarDepan(firstTwoWords, gelarDepan, maxLengthChar);
        else if (firstTwoWords != null) {
            value = firstTwoWords.substring(0, maxLengthChar);
        }
        //value = (value.trim().equals("") ? "" : (value + " ")) + firstTwoWords;
        if (value.length() <= maxLengthChar)
            value2 = getValidGelarBelakang(value, gelarBelakang, maxLengthChar);
        if (value2 != null && value2.length() <= maxLengthChar)
            return value2;

        return value;
    }

    private static String getValidGelarBelakang(String value, String gelarBelakang, int maxLengthChar) {
        String belakangs = null;
        StringBuilder result = null;
        StringTokenizer tokenizer = new StringTokenizer(gelarBelakang);
        if (tokenizer.countTokens() > 0) {
            belakangs = value + " " + tokenizer.nextToken();
            if (belakangs.length() <= maxLengthChar) {
                result = new StringBuilder(belakangs);
                StringBuilder resultTemp = new StringBuilder(result);
                while (tokenizer.hasMoreTokens() && result.length() <= maxLengthChar) {
                    resultTemp.append(" ").append(tokenizer.nextToken());
                    if (resultTemp.length() <= maxLengthChar)
                        result = new StringBuilder(resultTemp);
                }
            }
        }
        return result == null ? null : result.toString();
    }

    private static String getValidGelarDepan(String value, String gelarDepan, int maxLengthChar) {
        StringBuilder depans = null;
        StringBuilder result = null;
        StringTokenizer tokenizer = new StringTokenizer(gelarDepan);
        if (tokenizer.countTokens() > 0) {
            depans = new StringBuilder(tokenizer.nextToken());
            String tempValue = depans + " " + value;
            if (tempValue.length() <= maxLengthChar) {
                result = new StringBuilder(tempValue);
                String resultTemp;
                while (tokenizer.hasMoreTokens() && result.length() <= maxLengthChar) {
                    depans.append(" ").append(tokenizer.nextToken());
                    resultTemp = depans + value;
                    if (resultTemp.length() <= maxLengthChar)
                        result = new StringBuilder(resultTemp);
                }
            }
        }
        return result == null ? value : result.toString();
    }
}



