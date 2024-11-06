package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.LogPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.MesinPresensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.LogPresensiService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.MesinPresensiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/iclock")
public class IclockController {

    @Autowired
    private MesinPresensiService mesinPresensiService;

    @Autowired
    private LogPresensiService logPresensiService;

    @GetMapping(value = "/cdata")
    public ResponseEntity<String> cdataGet(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        log.info("cdataGet qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("cdataGet parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("cdataGet header k {} v {}", K, V);
        });

        String sn = request.getParameter("SN");
        if (!sn.isEmpty()) {
            MesinPresensi mesinPresensi = mesinPresensiService.findBySn(sn);
            if (mesinPresensi != null) {
                log.info("SN found {}", sn);
                return new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("NO", HttpStatus.OK);
    }

    @PostMapping(value = "/cdata")
    public ResponseEntity<String> cdataPost(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("cdataPost qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("cdataPost parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("cdataPost header k {} v {}", K, V);
        });

        log.info("cdataPost data {}", data);

        String sn = request.getParameter("SN");
        String stamp = request.getParameter("Stamp");
        String table = request.getParameter("table");

        if (table.equals(AppConstant.ATTLOG)) {
            String[] linesData = data.split("\\r?\\n");
            List<String> lines = Arrays.stream(linesData).collect(Collectors.toList());

            int i = 0;
            for (String d : lines) {
                log.info("cdataPost data ATTLOG : {}", d);

                String noAbsen = d.split("\\s")[0];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime waktu = LocalDateTime.parse(d.split("\\s")[1] + " " + d.split("\\s")[2], formatter);
                String inOut = d.split("\\s")[3];
                Integer verify = Integer.valueOf(d.split("\\s")[4]);
                String op = d.split("\\s")[5];

                log.info("no absen: {} tgl: {}, inOut: {}, verify: {}, sn: {}, op: {}, stamp: {}", noAbsen, waktu, inOut, verify, sn, op, stamp);
                LogPresensi logPresensi = logPresensiService.setData(noAbsen, waktu, inOut, verify, sn, op, stamp);
                logPresensiService.save(logPresensi);
                log.info("cdataPost save {}", logPresensi.getId());

                i++;
            }

            if (i > 0) {
                return new ResponseEntity<>("OK: " + i, HttpStatus.OK);
                //return "OK: " + i;
            }
        } else if (table.equals(AppConstant.OPLOG)) {
            String[] linesData = data.split("\\r?\\n");
            List<String> lines = Arrays.stream(linesData).collect(Collectors.toList());

            int i = 0;
            for (String d : lines) {
                log.info("cdataPost data OPLOG : {}", d);
                i++;
            }

            if (i > 0) {
                return new ResponseEntity<>("OK: " + i, HttpStatus.OK);
                //return "OK: " + i;
            }
        }

        return new ResponseEntity<>("NO", HttpStatus.OK);
        //return "OK";
    }

    @GetMapping(value = "/getrequest")
    public ResponseEntity<String> getrequestGet(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        log.info("getrequestGet qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("getrequestGet parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("getrequestGet header k {} v {}", K, V);
        });

        String sn = request.getParameter("SN");
        String info = request.getParameter("INFO");
        if ((sn != null && !sn.isEmpty()) && (info != null && !info.isEmpty())) {
            //mengirim rekap informasi terkini dari mesin ke server via parameter
            log.info("getrequestGet data terkini: {}", info);

            return new ResponseEntity<>("OK", HttpStatus.OK);
            //return "OK";
        }

        return new ResponseEntity<>("NO", HttpStatus.OK);
        //return "NO";
    }


    @PostMapping(value = "/getrequest")
    public ResponseEntity<String> getrequestPost(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("getrequestPost qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("getrequestPost parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("getrequestPost header k {} v {}", K, V);
        });

        log.info("getrequestPost data {}", data);

        return new ResponseEntity<>("NO", HttpStatus.OK);
        //return "NO";
    }

    @GetMapping("/edata")
    public String edataGet(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("edataGet qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("edataGet parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("edataGet header k {} v {}", K, V);
        });

        log.info("edataGet data {}", data);

        return "NO";
    }

    @PostMapping(value = "/edata")
    public String edataPost(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("edataPost qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("edataPost parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("edataPost header k {} v {}", K, V);
        });

        log.info("edataPost data {}", data);

        String sn = request.getParameter("SN");
        String stamp = request.getParameter("Stamp");

        String[] linesData = data.split("\\r?\\n");
        List<String> lines = Arrays.stream(linesData).collect(Collectors.toList());

        int i = 0;
        for (String d : lines) {
            log.info("data : {}", d);

            String noAbsen = d.split("\\s")[0];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime waktu = LocalDateTime.parse(d.split("\\s")[1] + " " + d.split("\\s")[2], formatter);
            String inOut = d.split("\\s")[3];
            Integer verify = Integer.valueOf(d.split("\\s")[4]);
            String op = d.split("\\s")[5];

            log.info("no absen: {} tgl: {}, inOut: {}, verify: {}, sn: {}, op: {}, stamp: {}", noAbsen, waktu, inOut, verify, sn, op, stamp);
            LogPresensi logPresensi = logPresensiService.setData(noAbsen, waktu, inOut, verify, sn, op, stamp);
            logPresensiService.save(logPresensi);
            log.info("cdataPost save {}", logPresensi.getId());

            i++;
        }

        if (i > 0) {
            return "OK: " + i;
        }
        return "NO";
    }

    @GetMapping("/devicecmd")
    public String devicecmdGet(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("devicecmdGet qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("devicecmdGet parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("devicecmdGet header k {} v {}", K, V);
        });

        log.info("devicecmdGet data {}", data);

        return "NO";
    }

    @PostMapping(value = "/devicecmd")
    public String devicecmdPost(HttpServletRequest request, @RequestBody String data, @RequestHeader Map<String, String> headers) {
        log.info("devicecmdPost qs {}", request.getQueryString());
        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((K, V) -> {
            log.info("devicecmdPost parameter k {} v {}", K, V);
        });

        headers.forEach((K, V) -> {
            log.info("devicecmdPost header k {} v {}", K, V);
        });

        log.info("devicecmdPost data {}", data);

        return "NO";
    }
}
