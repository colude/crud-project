package com.springboot.crud.plasse.model;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.springboot.crud.plasse.validator.BirthDateConstraint;
import com.springboot.crud.plasse.validator.CountryConstraint;
import com.springboot.crud.plasse.validator.GenderConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
	
	private Long id;
	
	@NotBlank(message = "userName should not be null")
    @Size(min = 3, max = 30, message = "userName should be of 3 - 30 characters")
	private String userName;
	
	@BirthDateConstraint(message = "invalid birthdate")
	private String birthDate;
	
	@CountryConstraint(message = "invalid country")
	private String country;
	
	private String phoneNumber;

	@GenderConstraint(message = "invalid gender")
    private String gender;

}
