package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.dto.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    public List<ValidationError> convertBindingResult(BindingResult bindingResult) {
        List<ValidationError> errors = new ArrayList<>();
        if (bindingResult != null) {
            bindingResult.getFieldErrors().forEach(data -> {
                String message = data.getDefaultMessage();
                String field = data.getField();

                ValidationError error = new ValidationError();
                error.setDefaultMessage(message);
                error.setField(field);

                errors.add(error);
            });
        }

        return errors;
    }
}
