package com.company.userapp.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private Pattern passwordPattern;

    private Environment environment;

    @Autowired
    public PasswordValidator(Environment environment) {
        this.environment = environment;
        this.passwordPattern = Pattern.compile(environment.getProperty("user.password.validation", ".+"));
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return passwordPattern.matcher(s).reset().matches();
    }
}
