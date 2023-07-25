package com.springboot.crud.plasse.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.springboot.crud.plasse.validator.utils.ValidatorUtils;


public class CountryValidator implements ConstraintValidator<CountryConstraint, String> {

	@Override
	public boolean isValid(final String valueToValidate, final ConstraintValidatorContext context) {
		if(valueToValidate == null) {
			ValidatorUtils.setNewErrorMessage("country should not be null", context);
			return false;
		}
		
		Pattern pattern = Pattern.compile("[a-zA-Z\\\\s']+");
        Matcher matcher = pattern.matcher(valueToValidate);
        if(!matcher.matches()) {
        	ValidatorUtils.setNewErrorMessage("country should content only alphabetical characters", context);
			return false;
        }

		if(!"France".equalsIgnoreCase(valueToValidate)) {
			ValidatorUtils.setNewErrorMessage("must be french", context);
			return false;
		}
		
		return true;
	}
}