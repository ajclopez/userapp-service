package com.company.userapp.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    private Environment environment;

    private Pattern emailPattern;

    @Autowired
    public EmailValidator(Environment environment) {
        this.environment = environment;
        this.emailPattern = Pattern.compile(
                this.environment.getProperty("user.email.validation",
                "[_A-Za-z0-9-]+((\\.|\\+)?[_A-Za-z0-9-]+)*@[A-Za-z0-9]+((\\.|-)?[A-Za-z0-9]+)*(\\.[A-Za-z]{2,6})$"));
    }


    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return emailPattern.matcher(s).reset().matches();
    }
}
