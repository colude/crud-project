package com.springboot.crud.plasse.validator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.springboot.crud.plasse.validator.utils.ValidatorUtils;


public class BirthDateValidator implements ConstraintValidator<BirthDateConstraint, String> {
		
	@Override	
	public boolean isValid(final String valueToValidate, final ConstraintValidatorContext context) {
		if(valueToValidate == null) {
			ValidatorUtils.setNewErrorMessage("birthDate should not be null", context);
			return false;
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate ;
		try {
			birthDate = LocalDate.parse(valueToValidate, formatter);
		} catch ( DateTimeException e) {
			ValidatorUtils.setNewErrorMessage("birthDate should respect format yyyy-MM-dd", context);
			return false;
		}
		
		LocalDate now = LocalDate.now(ZoneId.systemDefault());
		if(birthDate.isAfter(now)) {
			ValidatorUtils.setNewErrorMessage("birthDate should not be a future date", context);
			return false;
		}

		boolean isMajeur = ChronoUnit.YEARS.between( birthDate , LocalDate.now()) >= 18;
		if(!isMajeur) {
			ValidatorUtils.setNewErrorMessage("client should be at least 18 years old", context);
			return false;
		}

		return true;
	}
}