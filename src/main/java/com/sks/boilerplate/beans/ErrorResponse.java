package com.sks.boilerplate.beans;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.VndErrors.VndError;

import lombok.Getter;

public class ErrorResponse extends VndError {

	@Getter
	private Object data;

	public ErrorResponse(Object data, String message, Link link) {
		super(message, message, link);
		this.data = data;
	}

}
