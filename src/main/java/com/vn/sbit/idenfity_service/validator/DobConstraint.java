package com.vn.sbit.idenfity_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) //phạm vi trong biến
@Retention(RetentionPolicy.RUNTIME) //khi runtime sẽ thực hiện
@Constraint( // class chịu trách nhiệm valid
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
    String message() default "Invalid data of birth";

    int min() ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
