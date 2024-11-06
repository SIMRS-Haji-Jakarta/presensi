package id.ac.uinjkt.pustipanda.aplikasipresensi.util;

public class CommonUtils {
    public static boolean isExists(String name) {
//        File f = new File(name);
//        return f.exists() && !f.isDirectory();
        return name != null && !name.isEmpty();
    }

    public static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
