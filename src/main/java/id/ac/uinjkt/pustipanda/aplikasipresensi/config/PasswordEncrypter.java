package id.ac.uinjkt.pustipanda.aplikasipresensi.config;

import id.ac.uinjkt.pustipanda.aplikasipresensi.services.DesEncrypter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class PasswordEncrypter implements PasswordEncoder {
    @Value("${app.aes.encoder}")
    private String secret;

    @Autowired
    private static DesEncrypter desEncrypter;

    public String encryptMethod(String str) {
        desEncrypter = new DesEncrypter(secret);
        return desEncrypter.encrypt(str);
    }

    public String decryptMethod(String str) {
        desEncrypter = new DesEncrypter(secret);
        return desEncrypter.decrypt(str);
    }

    @Override
    public String encode(CharSequence pwd) {
        return encryptMethod(pwd.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword.toString().equals(decryptMethod(encodedPassword)))
            return true;

        return false;
    }
}
