package com.springboot.crud.plasse.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.crud.plasse.model.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Employee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(name = "user_name")
	private String userName;
	
    @Column(name = "birth_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
	
    @Column(name = "country")
	private String country;
	
    @Column(name = "phone_number")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

}
