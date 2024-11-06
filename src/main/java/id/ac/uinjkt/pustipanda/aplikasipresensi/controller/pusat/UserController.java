package id.ac.uinjkt.pustipanda.aplikasipresensi.controller.pusat;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.UserDto;
import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Role;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.User;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.RoleService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.UserService;
import id.ac.uinjkt.pustipanda.aplikasipresensi.services.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/pusat/pengguna")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping(value = {"/", "/list"})
    public String showList(ModelMap mm,
                           @RequestParam(value = "q", required = false) String search,
                           @RequestParam(value = "role", required = false) Role role, @PageableDefault(size = 10) Pageable page) {
        Page<User> result = userService.getAllDataBySearchParam(search, role, page);
        log.info("search: {}, role: {}", search, role);

        mm.addAttribute("q", search);
        mm.addAttribute("role", role);
        mm.addAttribute("listRole", roleService.findAllRole());
        mm.addAttribute("data", result);

        return AppConstant.TEMPLATE_USER;
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap mm) {
        User user = new User();
        if (StringUtils.hasText(id)) {
            Optional<User> o = userService.findByUserId(id);
            if (o.isPresent()) {
                user = o.get();
            }
        }

        mm.addAttribute("listRole", roleService.findAllRole());
        mm.addAttribute("data", user.getUserId() == null ? new UserDto() : user);

        return AppConstant.TEMPLATE_USER + " :: modalForm";
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/form")
    public String updateForm(@ModelAttribute @Valid UserDto o, BindingResult errors, ModelMap mm, RedirectAttributes ra) {
        log.info("error : {}", errors);

        if (errors.hasErrors()) {
            log.info("errors {}", errors);
            List<ValidationError> err = validationService.convertBindingResult(errors);
            ra.addFlashAttribute(AppConstant.MESSAGE, err.stream().map(ValidationError::getDefaultMessage)
                    .collect(Collectors.joining("<br />")));

            return AppConstant.LINK_USER;
        }

        Optional<User> oUser = userService.findByUserId(o.getUserId());
        if (oUser.isPresent()) {
            Optional<User> checkUserIsExist = userService.findUserByUserIdNot(o.getUserId(), o.getUserId());
            if (checkUserIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Username has been used.");

                return AppConstant.LINK_USER;
            }

            BeanUtils.copyProperties(o, oUser.get(), "pegawai", "userPassword");
            if (StringUtils.hasText(o.getNewPassword())) {
                if (o.getNewPassword().equals(o.getPasswordConfirm()))
                    oUser.get().setUserPassword(passwordEncoder.encode(o.getNewPassword()));
                else {
                    ra.addFlashAttribute(AppConstant.MESSAGE, "Password not match!");

                    return AppConstant.LINK_USER;
                }
            }

            userService.save(oUser.get());
        } else {
            Optional<User> checkUserIsExist = userService.findByUserId(o.getUserId());
            if (checkUserIsExist.isPresent()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "User ID sudah digunakan.");

                return AppConstant.LINK_USER;
            }

            if (o.getUserPassword() == null || o.getUserPassword().isEmpty()) {
                ra.addFlashAttribute(AppConstant.MESSAGE, "Password harus diisi");

                return AppConstant.LINK_USER;
            }

            User user = new User();
            BeanUtils.copyProperties(o, user);
            user.setUserPassword(passwordEncoder.encode(o.getUserPassword()));
            userService.save(user);
        }

        return AppConstant.LINK_USER;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/aktif")
    public String yesForm(@RequestParam User user) {
        if (user == null) {
            log.warn("Set aktif user null");
            return AppConstant.LINK_USER;
        }

        user.setAktif(true);
        userService.save(user);

        return AppConstant.LINK_USER;
    }

    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @PostMapping("/nonaktif")
    public String noForm(@RequestParam User user) {
        if (user == null) {
            log.warn("Set non aktif user null");
            return AppConstant.LINK_USER;
        }

        user.setAktif(false);
        userService.save(user);

        return AppConstant.LINK_USER;
    }


    @PreAuthorize("hasAnyAuthority('am','pegpst')")
    @GetMapping("/delete")
    public String deleteData(@RequestParam(value = "id", required = false) String id, RedirectAttributes ra) {
        Optional<User> user = userService.findByUserId(id);
        if (user.isPresent()) {
            try {
                userService.delete(user.get());
            } catch (Exception e) {
                log.error("error delete user: {}", e.getMessage());
                ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak dapat dihapus");
            }

        } else {
            ra.addFlashAttribute(AppConstant.MESSAGE, "Data tidak ditemukan");
        }

        return AppConstant.LINK_USER;
    }
}
