package com.springboot.crud.plasse.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.crud.plasse.advice.annotation.TrackExecutionTime;
import com.springboot.crud.plasse.advice.annotation.TrackLoggerTime;
import com.springboot.crud.plasse.entity.Employee;
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

	@PostMapping("/save")
	@TrackExecutionTime
	@TrackLoggerTime
	@ApiOperation(value = "To create or update an employee by passing an EmployeeDto")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "A new employee has been created successfully"),
			@ApiResponse(code = 202, message = "An employee has been updated successfully"),
			@ApiResponse(code = 500, message = "Internal Server Error") 
	})
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Employee> saveEmployee(@Valid @RequestBody EmployeeDto employeeUpdate) {	
		try {
			Employee employeeToSave = modelMapper.map(employeeUpdate, Employee.class);	
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate birthDate = LocalDate.parse(employeeUpdate.getBirthDate(), formatter);
				
			Optional<Employee> employeeData = employeeService.findByUserName(employeeUpdate.getUserName());
			employeeToSave.setBirthDate(birthDate);

			if (employeeData.isPresent()) {
				employeeToSave.setId(employeeData.get().getId());
				employeeToSave = employeeService.saveEmployee(employeeToSave);
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(employeeToSave);
			} else {
				employeeToSave = employeeService.saveEmployee(employeeToSave);
				return ResponseEntity.status(HttpStatus.CREATED).body(employeeToSave);
			}	
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
