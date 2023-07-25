package com.springboot.crud.plasse.validator.utils;

import javax.validation.ConstraintValidatorContext;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatorUtils {
	
	public static void setNewErrorMessage(String newErrorMessage, ConstraintValidatorContext context) {
	    context.disableDefaultConstraintViolation();
	    context.buildConstraintViolationWithTemplate(newErrorMessage).addConstraintViolation();
	}

}
