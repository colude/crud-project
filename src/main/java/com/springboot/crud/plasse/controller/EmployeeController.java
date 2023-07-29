package com.springboot.crud.plasse.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.crud.plasse.advice.annotation.TrackExecutionTime;
import com.springboot.crud.plasse.advice.annotation.TrackLoggerTime;
import com.springboot.crud.plasse.entity.Employee;
import com.springboot.crud.plasse.exception.ApiRequestException;
import com.springboot.crud.plasse.exception.UserNotFoundException;
import com.springboot.crud.plasse.model.EmployeeDto;
import com.springboot.crud.plasse.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping( "api/v1/users")
@Api(value = "Set of endpoints for Creating, Retrieving, Updating and Deleting of employees.")
public class EmployeeController {

    private ModelMapper modelMapper = new ModelMapper();
    
	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To access all employees")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The request has succeeded"),
			@ApiResponse(code = 404, message = "No record in the database") 
	})
	public ResponseEntity<List<Employee>> getEmployees(){
		List<Employee> employees = employeeService.getEmployees();

		if(!employees.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employees);
		} else {
			throw new UserNotFoundException("No record in the database");
		}				
	}
	
	@GetMapping ("/{id}")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To get an employee by id")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The request has succeeded"),
			@ApiResponse(code = 404, message = "User not found")
	})
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id){
		Optional<Employee> employeeData =  employeeService.findById(id);
		return employeeData.map(response -> ResponseEntity.ok().body(employeeData.get()))
				.orElseThrow(() -> new UserNotFoundException("User with id " + id + " was not found"));
    }
	
	
	@GetMapping ("/userName/{userName}")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To get an employee by userName")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The request has succeeded"),
			@ApiResponse(code = 404, message = "User not found")
	})
	public ResponseEntity<Employee> getEmployeeByUserName(@PathVariable String userName) {
		Optional<Employee> employeeData = employeeService.findByUserName(userName); 
		return employeeData.map(response -> ResponseEntity.ok().body(employeeData.get()))
				.orElseThrow(() -> new UserNotFoundException("User with userName " + userName + " was not found"));
	}
	
	@PutMapping("/update/{id}")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To update an employee by passing an EmployeeDto")
	@ApiResponses(value = {
			@ApiResponse(code = 202, message = "An employee has been updated successfully")
	})
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public ResponseEntity<Employee> updateEmployee(
			@PathVariable(value = "id", required = false) final Long id,
			@Valid @RequestBody EmployeeDto employeeUpdate) {	
		if(employeeUpdate.getId() == null) {
			throw new ApiRequestException("id cannot be null");
		}
		if (!Objects.equals(id, employeeUpdate.getId())) {
            throw new ApiRequestException("ids don't match");
        }
		if(!employeeService.findById(employeeUpdate.getId()).isPresent()) {
			throw new ApiRequestException("entity not found");
		}
			
		Employee employeeToSave = modelMapper.map(employeeUpdate, Employee.class);	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse(employeeUpdate.getBirthDate(), formatter);
		employeeToSave.setBirthDate(birthDate);
		employeeToSave = employeeService.saveEmployee(employeeToSave);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(employeeToSave);
	}
	
	@PostMapping("/save")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To create an employee by passing an EmployeeDto")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "A new employee has been created successfully")
	})
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Employee> saveEmployee(@Valid @RequestBody EmployeeDto employeeUpdate) {	
		if (employeeUpdate.getId() != null) {
			throw new ApiRequestException("A new employee cannot already have an ID");
		}
		Employee employeeToSave = modelMapper.map(employeeUpdate, Employee.class);	

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse(employeeUpdate.getBirthDate(), formatter);
		employeeToSave.setBirthDate(birthDate);

		if (employeeService.findByUserName(employeeToSave.getUserName()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} else {
			employeeToSave = employeeService.saveEmployee(employeeToSave);
			return ResponseEntity.status(HttpStatus.CREATED).body(employeeToSave);
		}	
	}
	
	@DeleteMapping("/{userName}")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To delete an employee by userName")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Employee has been deleted successfully"),
			@ApiResponse(code = 404, message = "Employee not found"),
			@ApiResponse(code = 500, message = "Internal Server Error")

	})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<Employee> deleteEmployee(@PathVariable String userName) {
		try {
			Optional<Employee> employeeData = employeeService.findByUserName(userName);
			
			if (employeeData.isPresent()) {
				employeeService.deleteByUserName(userName);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
