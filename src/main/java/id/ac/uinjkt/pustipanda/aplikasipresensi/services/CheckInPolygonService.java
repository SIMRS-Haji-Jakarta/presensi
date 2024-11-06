package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.geom.Path2D;
import java.util.ArrayList;

@Slf4j
@Service
public class CheckInPolygonService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(CheckInPolygonService.class);

    private Double userLat;
    private Double userLon;

    private String area;

    public String getCoord() {
        return area;
    }

    public void setCoord(String coord) {
        this.area = coord;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLon() {
        return userLon;
    }

    public void setUserLon(Double userLon) {
        this.userLon = userLon;
    }

    public CheckInPolygonService() {
    }

    public CheckInPolygonService(Double userLat, Double userLon, String area) {
        this.userLat = userLat;
        this.userLon = userLon;
        this.area = area;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public boolean isInPolygon() {

        String[] test = this.area.split(";");
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lonList = new ArrayList<>();

        for (String t : test) {
            String[] latlng = t.split(",");
            latList.add(Double.parseDouble(latlng[0]));
            lonList.add(Double.parseDouble(latlng[1]));
        }

        log.info("Lat list {}, Lon list {}", latList, lonList);

        Double[] latpoints = latList.toArray(new Double[latList.size()]);
        Double[] lonpoints = lonList.toArray(new Double[lonList.size()]);

        log.info("latpoints {}, lonpoints {}", latpoints, lonpoints);

        Path2D poly = new Path2D.Double();
        for (int i = 0; i < latpoints.length; i++) {
            if (i == 0) {
                poly.moveTo(latpoints[i], lonpoints[i]);
            } else {
                poly.lineTo(latpoints[i], lonpoints[i]);
            }
        }
        poly.closePath();

        log.info("Ada di polygon? {}", poly.contains(this.userLat, this.userLon));

        return poly.contains(this.userLat, this.userLon);
    }

}
