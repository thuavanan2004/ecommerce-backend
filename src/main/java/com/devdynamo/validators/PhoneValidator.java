package com.devdynamo.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public void initialize(PhoneNumber phoneNumberNo) {
    }
    @Override
    public boolean isValid(String phoneNo, ConstraintValidatorContext cvc) {
        if(phoneNo == null){
            return false;
        }
        if (phoneNo.matches("0\\d{9}") || phoneNo.matches("84\\d{9}") || phoneNo.matches("\\+84\\d{9}")) {
            return true;
        }
        return false;
    }
}
