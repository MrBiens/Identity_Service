package com.vn.sbit.idenfity_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
//auto {min}
public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int min;
    //get from request
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    //check true or false according to regulation
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)){
            return true;
        }
        long years=  ChronoUnit.YEARS.between(localDate, LocalDate.now()); // check request dob with date now
        return years >= min;
    }

}
