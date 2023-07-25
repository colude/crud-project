package com.springboot.crud.plasse.model;


public enum Gender {
	MALE("Male"),
	FEMALE("Female");

	private final String name;

	private Gender(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}