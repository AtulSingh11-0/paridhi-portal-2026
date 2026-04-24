package com.megatronix.paridhi.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	// pointcut that matches all Web REST Controllers
	@Pointcut ( "within(com.megatronix.paridhi.controller..*)" )
	public void controllerPointcut () {
	}

	// pointcut that matches all Business Services
	@Pointcut ( "within(com.megatronix.paridhi.service..*)" )
	public void servicePointcut () {
	}

	/**
	 * This wraps around the methods. It logs entry, executes the method,
	 * logs the exit/time, and catches/logs any exceptions invisibly!
	 */
	@Around ( "controllerPointcut() || servicePointcut()" )
	public Object logAround ( ProceedingJoinPoint joinPoint ) throws Throwable {
		String className = joinPoint.getSignature().getDeclaringTypeName();
		String methodName = joinPoint.getSignature().getName();

		log.debug("ENTER | Class: {} | Method: {}", className, methodName);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			// this executes the controller/service method
			Object result = joinPoint.proceed();

			stopWatch.stop();
			log.debug("EXIT | Class: {} | Method: {} | Duration: {}ms",
				className, methodName, stopWatch.getTotalTimeMillis());

			return result;

		} catch ( IllegalArgumentException e ) {
			log.error("ILLEGAL ARGUMENT | Class: {} | Method: {} | Error: {}",
				className, methodName, e.getMessage(), e);
			throw e;
		} catch ( Throwable e ) {
			log.error("EXCEPTION | Class: {} | Method: {} | Error: {}",
				className, methodName, e.getMessage(), e);
			throw e;
		}
	}
}