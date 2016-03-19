package com.sks.boilerplate.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public enum ErrorKeys {
	/**
	 * If the user does not have a valid security
	 */
	ACCESS_DENIED(HttpStatus.FORBIDDEN),
	/**
	 * In case the record already exists
	 */
	CONFLICTING_RECORD(HttpStatus.CONFLICT),
	/**
	 * The unprocessable entity
	 */
	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),
	/**
	 * Internal server Error
	 */
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

	@Getter
	private HttpStatus status;

	ErrorKeys(HttpStatus status) {
		this.status = status;
	}

}
