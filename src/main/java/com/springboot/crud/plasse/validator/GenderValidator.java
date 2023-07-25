package com.springboot.crud.plasse.validator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.springboot.crud.plasse.model.Gender;
import com.springboot.crud.plasse.validator.utils.ValidatorUtils;


public class GenderValidator implements ConstraintValidator<GenderConstraint, String> {
		
	@Override	
	public boolean isValid(final String valueToValidate, final ConstraintValidatorContext context) {
		if(valueToValidate == null) {
			return true;
		}
		
		List<String> genderList = Stream.of(Gender.values()).map(Enum::name).collect(Collectors.toList());

		if(!genderList.contains(valueToValidate)) {
			ValidatorUtils.setNewErrorMessage("values accepted for Enum class:  MALE or FEMALE", context);
			return false;
		}

		return true;
	}
}