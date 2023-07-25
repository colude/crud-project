package com.springboot.crud.plasse.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggerAdvice {
	
	//java-8-date-time-type-java-time-instant-not-supported-by-default-issue
	private ObjectMapper mapper =  JsonMapper.builder().addModule(new JavaTimeModule()).build();

	@Around("@annotation(com.springboot.crud.plasse.advice.annotation.TrackLoggerTime)")
	public Object trackerLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String methodName = proceedingJoinPoint.getSignature().getName();
		String className = proceedingJoinPoint.getTarget().getClass().toString();
		Object[] args = proceedingJoinPoint.getArgs();
		log.info("[LoggerAdvice] The method invoked " + className + " : " + methodName + "() with arguments : " + this.mapper.writeValueAsString(args));
		Object o = proceedingJoinPoint.proceed();
		log.info( "LoggerAdvice] The method invoked " + className + " : " + methodName + "() with Response : " + this.mapper.writeValueAsString(o));
		return o;
	}
	
	@AfterThrowing(value="execution(* com.springboot.crud.plasse.controller.EmployeeController.*(..))", throwing="ex")  
	public void afterThrowingAdvice(JoinPoint jp, Throwable ex) throws JsonProcessingException{
		Object[] args = jp.getArgs();
		log.info("[LoggerAdvice] The method invoked " + jp.getSignature() + " throws a " + ex.getClass().getName() + " with arguments : " + this.mapper.writeValueAsString(args));
	}	
}
