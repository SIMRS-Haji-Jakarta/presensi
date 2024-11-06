package id.ac.uinjkt.pustipanda.aplikasipresensi.util;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;

public class JpaUtil {
    public static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }

    public static List<Integer> getListTahun() {
        List<Integer> tahuns = new LinkedList<>();
        Integer currentYear = Year.now().getValue();

        for (int i = currentYear - 2; i <= currentYear + 3; i++) {
            tahuns.add(i);
        }

        return tahuns;
    }
}
