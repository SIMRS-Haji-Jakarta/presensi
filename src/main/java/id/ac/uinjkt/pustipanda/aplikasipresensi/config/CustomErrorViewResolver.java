package id.ac.uinjkt.pustipanda.aplikasipresensi.config;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
public class CustomErrorViewResolver implements ErrorViewResolver {
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String message = getExceptionMessage(throwable, statusCode);

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", request.getAttribute("javax.servlet.error.status_code"));
        mav.addObject("message", message);

        return mav;
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        /*
        String message = Optional.ofNullable(throwable.getMessage()).orElseGet(() -> {
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
            return httpStatus.getReasonPhrase();
        });

        return message;
         */
        if (throwable != null) {
            return throwable.getMessage();
        }

        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }
}
