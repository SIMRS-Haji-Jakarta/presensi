package id.ac.uinjkt.pustipanda.aplikasipresensi.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Async
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? Optional.of("public") : Optional.of(auth.getName());
    }
}
