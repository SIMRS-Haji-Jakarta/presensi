package id.ac.uinjkt.pustipanda.aplikasipresensi.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class LoginController {
    @RequestMapping(value = {"/", "/login"})
    public String login(ModelMap mm) {

        mm.addAttribute("error", true);
        return "login";
    }

    @RequestMapping("/map")
    public String map() {
        return "map";
    }

    @RequestMapping("/app-ads.txt")
    @ResponseBody
    public ResponseEntity<String> appAds() {
        return new ResponseEntity<>("google.com, pub-4205682745099866, DIRECT, f08c47fec0942fa0", HttpStatus.OK);
    }
}
