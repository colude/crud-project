package com.springboot.crud.plasse.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.springboot.crud.plasse.CrudApplication;
import com.springboot.crud.plasse.entity.Employee;
import com.springboot.crud.plasse.exception.UserNotFoundException;
import com.springboot.crud.plasse.model.EmployeeDto;
import com.springboot.crud.plasse.model.Gender;
import com.springboot.crud.plasse.service.EmployeeService;

@Profile("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrudApplication.class)
public class EmployeeControllerTest {
	
	private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

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
	
	@Autowired
	private EmployeeController employeeController;

	@MockBean
	private EmployeeService employeeService;
	
	@MockBean
    private ModelMapper modelMapper;
	
    private Employee employee;
    
    @Autowired
    private EntityManager em;

	@BeforeEach
    public void initTest() {
        this.employee = createEntity(this.em);
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
    
    
	@Test
	public void shouldGetSize() {
		Employee employeeOne = Employee.builder()
				.id(1L)
	            .userName(DEFAULT_USERNAME)
	            .birthDate(DEFAULT_BIRTH_DATE)
	            .country(DEFAULT_COUNTRY)
	            .phoneNumber(DEFAULT_PHONE_NUMBER)
	            .gender(DEFAULT_GENDER)
	            .build();
		Employee employeeTwo = Employee.builder()
				.id(2L)
	            .userName(UPDATED_USERNAME)
	            .birthDate(DEFAULT_BIRTH_DATE)
	            .country(DEFAULT_COUNTRY)
	            .phoneNumber(DEFAULT_PHONE_NUMBER)
	            .gender(DEFAULT_GENDER)
	            .build();
		List<Employee> list = Arrays.asList(employeeOne, employeeTwo);
		Mockito.when(employeeService.getEmployees()).thenReturn(list);

		ResponseEntity<List<Employee>> result = employeeController.getEmployees();
		assertEquals( 2 , result.getBody().size());
	}
	
	@Test
	public void shouldGetEmployeeByUserNameWithException() {		
	    Exception exception = Assertions.assertThrows(UserNotFoundException.class, () -> employeeController.getEmployeeByUserName("toto"));
		assertEquals( "User with userName toto was not found" , exception.getMessage());
	}
	
	@Test
	public void shouldCreateEmployee() {		
		EmployeeDto employeeDto = new EmployeeDto(null, DEFAULT_USERNAME, DEFAULT_BIRTH_DATE_STR, DEFAULT_COUNTRY, DEFAULT_PHONE_NUMBER, DEFAULT_GENDER.toString());
		ResponseEntity<Employee> employeeCreated = employeeController.saveEmployee(employeeDto);
		assertEquals( 201 , employeeCreated.getStatusCodeValue());
	}
	
	@Test
	public void shouldUpdateEmployee() {	
		EmployeeDto employeeDto = new EmployeeDto(1L, UPDATED_USERNAME, UPDATED_BIRTH_DATE_STR, UPDATED_COUNTRY, UPDATED_PHONE_NUMBER, UPDATED_GENDER.toString());

		Employee employee = Employee.builder()
	            .userName(UPDATED_USERNAME)
	            .birthDate(UPDATED_BIRTH_DATE)
	            .country(DEFAULT_COUNTRY)
	            .phoneNumber(DEFAULT_PHONE_NUMBER)
	            .gender(DEFAULT_GENDER)
	            .build();
		
		Optional<Employee> employeeData = Optional.of(employee);
		Mockito.when(employeeService.findByUserName(employeeDto.getUserName())).thenReturn(employeeData);
	
		ResponseEntity<Employee> employeeUpdated = employeeController.saveEmployee(employeeDto);
		assertEquals( 202 , employeeUpdated.getStatusCodeValue());
	}
	
	@Test
	public void shouldDeleteEmployee() {	
		Optional<Employee> employeeData = Optional.of(this.employee);
		Mockito.when(employeeService.findByUserName(employeeData.get().getUserName())).thenReturn(employeeData);
	
		ResponseEntity<Employee> employeeToDelete = employeeController.deleteEmployee(employeeData.get().getUserName());
		assertEquals( 204 , employeeToDelete.getStatusCodeValue());
	}
	
	@Test
	public void shouldDeleteEmployeeNotFound() {	
		Optional<Employee> employeeData = Optional.empty();
		Mockito.when(employeeService.findByUserName(DEFAULT_USERNAME)).thenReturn(employeeData);
	
		ResponseEntity<Employee> employeeToDelete = employeeController.deleteEmployee(DEFAULT_USERNAME);
		assertEquals( 404 , employeeToDelete.getStatusCodeValue());
	}
}
