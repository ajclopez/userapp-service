package com.company.userapp.validation;

import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "must have a valid format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}