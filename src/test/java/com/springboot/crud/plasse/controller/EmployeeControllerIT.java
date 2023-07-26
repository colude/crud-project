package com.springboot.crud.plasse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.crud.plasse.entity.Employee;
import com.springboot.crud.plasse.exception.ApiException;
import com.springboot.crud.plasse.exception.UserNotFoundException;
import com.springboot.crud.plasse.model.EmployeeDto;
import com.springboot.crud.plasse.model.Gender;
import com.springboot.crud.plasse.repository.EmployeeRepository;


/**
 * Integration tests for the EmployeeController REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class EmployeeControllerIT {

	private static final String DEFAULT_USERNAME = "AAAAAAAAAA";

	private static final LocalDate DEFAULT_BIRTH_DATE= LocalDate.of(1990, 1, 1);
	private static final LocalDate UPDATED_BIRTH_DATE= LocalDate.of(1991, 1, 1);
	
	private static final String DEFAULT_BIRTH_DATE_STR= "1990-01-01";
	private static final String UPDATED_BIRTH_DATE_STR= "1991-01-01";

	private static final Gender DEFAULT_GENDER= Gender.MALE;
	private static final Gender UPDATED_GENDER = Gender.FEMALE;
	
	private static final String DEFAULT_PHONE_NUMBER = "0600000000";
	private static final String UPDATED_PHONE_NUMBER = "0700000000";

	private static final String DEFAULT_COUNTRY = "France";
	private static final String UPDATED_COUNTRY = "France";

	private static final String ENTITY_API_URL = "/api/v1/users";
	private static final String ENTITY_API_URL_SAVE = "/api/v1/users/save";

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private MockMvc restEmployeeMockMvc;

	private Employee employee;
	
	private EmployeeDto employeeDto;
	
	private ObjectMapper objectMapper;
    
	@BeforeEach
	public void initTest() {
		this.employee = createEntity(this.em);
		this.employeeDto = createDto();
		this.objectMapper = TestUtil.createObjectMapper();
	}

	public static Employee createEntity(EntityManager em) {
		Employee employee = Employee.builder()
				.userName(DEFAULT_USERNAME)
				.birthDate(DEFAULT_BIRTH_DATE)
				.country(DEFAULT_COUNTRY)
				.phoneNumber(DEFAULT_PHONE_NUMBER)
				.gender(DEFAULT_GENDER)
				.build();
		return employee;
	}
	
	public static EmployeeDto createDto() {
		EmployeeDto employeeDto = EmployeeDto.builder()
				.id(1L)
				.userName(DEFAULT_USERNAME)
				.birthDate(DEFAULT_BIRTH_DATE_STR)
				.country(DEFAULT_COUNTRY)
				.phoneNumber(DEFAULT_PHONE_NUMBER)
				.gender(DEFAULT_GENDER.toString())
				.build();
		return employeeDto;
	}
	
	public static EmployeeDto updateDto() {
		EmployeeDto employeeDto = EmployeeDto.builder()
				.id(1L)
				.userName(DEFAULT_USERNAME)
				.birthDate(UPDATED_BIRTH_DATE_STR)
				.country(UPDATED_COUNTRY)
				.phoneNumber(UPDATED_PHONE_NUMBER)
				.gender(UPDATED_GENDER.toString())
				.build();
		return employeeDto;
	}
	
	
	@Test
	@Transactional
	public void shouldGetNoData() throws Exception {
		employeeRepository.flush();

		restEmployeeMockMvc.perform(get(ENTITY_API_URL )).andExpect(status().isNotFound());
	}
	
	@Test
	@Transactional
	void getEmployees() throws Exception {	
		employeeRepository.flush();

		createEmployee();
	
		restEmployeeMockMvc
		.perform(get(ENTITY_API_URL ))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value(2L))
		.andExpect(jsonPath("$[0].userName").value(DEFAULT_USERNAME))
		.andExpect(jsonPath("$[0].birthDate").value(DEFAULT_BIRTH_DATE.toString()))
		.andExpect(jsonPath("$[0].country").value(DEFAULT_COUNTRY))
		.andExpect(jsonPath("$[0].phoneNumber").value(DEFAULT_PHONE_NUMBER))
		.andExpect(jsonPath("$[0].gender").value(DEFAULT_GENDER.toString()));			 
	}	 

	@Test
	@Transactional
	void getNonExistingEmployeeByUserName() throws Exception {		 
		MvcResult result  = restEmployeeMockMvc
				.perform(get(ENTITY_API_URL + "/userName/{userName}", "toto"))
				.andExpect(status().is4xxClientError())
				.andReturn();
		Optional<UserNotFoundException> exception = Optional.ofNullable((UserNotFoundException) result.getResolvedException());
		exception.ifPresent( (ex) -> assertThat(ex, is(notNullValue())));
		exception.ifPresent( (ex) -> assertThat(ex, is(instanceOf(UserNotFoundException.class))));
	}
	
	@Test
	@Transactional
	void getNonExistingEmployeeById() throws Exception {		 
		MvcResult result  = restEmployeeMockMvc
				.perform(get(ENTITY_API_URL + "/{id}", 99L))
				.andExpect(status().is4xxClientError())
				.andReturn();
		Optional<UserNotFoundException> exception = Optional.ofNullable((UserNotFoundException) result.getResolvedException());
		exception.ifPresent( (ex) -> assertThat(ex, is(notNullValue())));
		exception.ifPresent( (ex) -> assertThat(ex, is(instanceOf(UserNotFoundException.class))));
	}
	
	@Test
	@Transactional
	void createEmployee() throws Exception {
		employeeRepository.flush();
	
		restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))
				.andExpect(status().isCreated());
	}
	
	@Test
	@Transactional
	void updateEmployee() throws Exception {
        employeeRepository.saveAndFlush(employee);
        
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        
        this.employeeDto = updateDto();

		restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))
				.andExpect(status().isAccepted());
		
		 // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getUserName()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testEmployee.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testEmployee.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testEmployee.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testEmployee.getGender()).isEqualTo(UPDATED_GENDER);
	}
	
	@Test
	@Transactional
	void checkUserNameIsRequired() throws Exception {
		this.employeeDto.setUserName(null);

		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().entrySet().stream().findFirst().get().getValue()).isEqualTo("userName should not be null");	
	}
	
	@Test
	@Transactional
	void checkUserNameSize() throws Exception {
		this.employeeDto.setUserName("to");

		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().entrySet().stream().findFirst().get().getValue()).isEqualTo("userName should be of 3 - 30 characters");
	}
	
	@Test
	@Transactional
	void checkBirthDateIsNotNull() throws Exception {
		this.employeeDto.setBirthDate(null);

		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().entrySet().stream().findFirst().get().getValue()).isEqualTo("birthDate should not be null");
	}
	
	@Test
	@Transactional
	void checkCountryMatchPattern() throws Exception {
		this.employeeDto.setCountry("007");

		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().entrySet().stream().findFirst().get().getValue()).isEqualTo("country should content only alphabetical characters");
	}

	@Test
	@Transactional
	void checkGenderIsValid() throws Exception {
		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content("{\r\n"
						+ "    \"userName\": \"ludo\",\r\n"
						+ "    \"birthDate\": \"1932-08-25\",\r\n"
						+ "    \"country\": \"France\",\r\n"
						+ "    \"gender\" : \"jkjk\"\r\n"
						+ "}"))        
				.andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().get("gender")).isEqualTo("values accepted for Enum class:  MALE or FEMALE");
	}

	@Test
	@Transactional
	void createEmployeeFailed() throws Exception {
		this.employeeDto.setUserName(null);
		this.employeeDto.setBirthDate("19856-55-99");
		this.employeeDto.setCountry("bulbizarre");
		
		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().get("userName")).isEqualTo("userName should not be null");
		assertThat(apiException.getErrors().get("birthDate")).isEqualTo("birthDate should respect format yyyy-MM-dd");
		assertThat(apiException.getErrors().get("country")).isEqualTo("must be french");
	}
	
	@Test
	@Transactional
	void createEmployeeFailedFutureBirthDate() throws Exception {
		this.employeeDto.setUserName(null);
		this.employeeDto.setBirthDate("2032-08-03");
		this.employeeDto.setCountry("bulbizarre");
		
		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().get("userName")).isEqualTo("userName should not be null");
		assertThat(apiException.getErrors().get("birthDate")).isEqualTo("birthDate should not be a future date");
		assertThat(apiException.getErrors().get("country")).isEqualTo("must be french");
	}
	
	@Test
	@Transactional
	void createEmployeeMultipleFailed() throws Exception {
		this.employeeDto.setUserName("p");
		this.employeeDto.setBirthDate("29024-09-25");
		this.employeeDto.setCountry("bulbizarre");
		this.employeeDto.setGender("aa");
		
		MvcResult result = restEmployeeMockMvc
				.perform(post(ENTITY_API_URL_SAVE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(this.employeeDto)))        
				.andExpect(status().is4xxClientError()).andReturn();

		String response = result.getResponse().getContentAsString();

		ApiException apiException = this.objectMapper.readValue(response, ApiException.class);
		assertThat(apiException.getCode()).isEqualTo(400);
		assertThat(apiException.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(apiException.getErrors().get("userName")).isEqualTo("userName should be of 3 - 30 characters");
		assertThat(apiException.getErrors().get("birthDate")).isEqualTo("birthDate should respect format yyyy-MM-dd");
		assertThat(apiException.getErrors().get("gender")).isEqualTo("values accepted for Enum class:  MALE or FEMALE");
		assertThat(apiException.getErrors().get("country")).isEqualTo("must be french");
	}
	
	@Test
	@Transactional
	void shouldDeleteUnknownEmployee() throws Exception {	
		MvcResult result = restEmployeeMockMvc
		.perform(delete(ENTITY_API_URL + "/" + DEFAULT_USERNAME )).andExpect(status().is4xxClientError()).andReturn();
		
		assertThat(result.getResponse().getStatus()).isEqualTo(404);
	}
	
	@Test
	@Transactional
	void shouldDeleteEmployee() throws Exception {	
		createEmployee();
		MvcResult result = restEmployeeMockMvc
		.perform(delete(ENTITY_API_URL + "/" + DEFAULT_USERNAME )).andExpect(status().is2xxSuccessful()).andReturn();
		
		assertThat(result.getResponse().getStatus()).isEqualTo(204);
	}
}
