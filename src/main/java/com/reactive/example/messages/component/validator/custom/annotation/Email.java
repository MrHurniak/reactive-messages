package com.reactive.example.messages.component.validator.custom.annotation;

import com.reactive.example.messages.component.validator.custom.processor.EmailValidationProcessor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidationProcessor.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

    boolean isNullable() default false;

    String message() default "Invalid email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
