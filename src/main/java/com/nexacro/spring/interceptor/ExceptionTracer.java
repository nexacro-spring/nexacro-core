package com.nexacro.spring.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTracer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTracer.class);

	public void trace(Exception exception) throws Exception {
		LOGGER.error(exception.getMessage(), exception);
	}
	
}
