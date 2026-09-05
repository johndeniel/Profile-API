package com.profile.api.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URL;

public class OptionalURLValidator implements ConstraintValidator<OptionalURL, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            new URL(value).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
