package com.springboot.crud.plasse.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAdvice {
	
	@Around("@annotation(com.springboot.crud.plasse.advice.annotation.TrackExecutionTime)")
	public Object trackTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object o = proceedingJoinPoint.proceed();
		long end = System.currentTimeMillis();
		log.info("[ExecutionTimeAdvice] The method " + proceedingJoinPoint.getSignature().getName() + " was executed in " + (end -start) + " ms");
		return o;
	}
	
	@AfterThrowing(value="execution(* com.springboot.crud.plasse.controller.EmployeeController.*(..))", throwing="ex")  
	public void afterThrowingAdvice(JoinPoint jp, Throwable ex){
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		log.info("[ExecutionTimeAdvice] The method " + jp.getSignature() + " throws a " + ex.getClass().getName() +" and was executed in " + (end -start) + " ms");
	}
}
