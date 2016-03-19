package com.sks.boilerplate.exception;

import org.springframework.http.HttpStatus;

import com.sks.boilerplate.enums.ErrorKeys;

import lombok.Getter;

public class ApplicationException extends RuntimeException {

	@Getter
	private ErrorKeys errorKey;

	@Getter
	private Object data;

	public ApplicationException(ErrorKeys errorKey) {
		this(errorKey, null);
	}

	public ApplicationException(Exception exception, ErrorKeys errorKey) {
		super(exception);
		this.errorKey = errorKey;
	}

	public ApplicationException(ErrorKeys errorKey, Object data) {
		super(errorKey.name());
		this.errorKey = errorKey;
		this.data = data;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public HttpStatus getStatusCode() {
		return this.errorKey.getStatus();
	}

}
