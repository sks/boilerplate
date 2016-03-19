package com.sks.boilerplate.controller;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sks.boilerplate.beans.ErrorResponse;
import com.sks.boilerplate.exception.ApplicationException;

import ch.qos.logback.classic.Logger;

@ControllerAdvice
public class ApplicationExceptionHandler {

	private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<VndError> catchApplicationException(ApplicationException exception,
			HttpServletRequest request) {
		this.logger.debug("Error while processing the request " + request.getRequestURI(), exception);
		final VndError vndError = new ErrorResponse(exception.getData(), exception.getMessage(), getLink(request));

		return new ResponseEntity<>(vndError, exception.getStatusCode());
	}

	@ExceptionHandler(value = EntityNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ErrorResponse catchEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
		this.logger.warn("Error while processing the request " + request.getRequestURI(), exception);
		return new ErrorResponse(null, "NOT_FOUND", getLink(request));
	}

	private static Link getLink(HttpServletRequest request) {
		return new Link(request.getRequestURL().toString());
	}
}
