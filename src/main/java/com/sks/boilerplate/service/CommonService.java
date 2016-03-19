package com.sks.boilerplate.service;

import javax.persistence.EntityNotFoundException;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.sks.boilerplate.enums.ErrorKeys;
import com.sks.boilerplate.exception.ApplicationException;

import ch.qos.logback.classic.Logger;

public abstract class CommonService {

	protected Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	protected static <T> T returnIfNotNull(T t) {
		throwIfNull(t);
		return t;
	}

	private static <T> void throwIfNull(T t) {
		if (null == t) {
			throw new EntityNotFoundException();
		}
	}

	public static UserDetails getCurrentUser() {
		final Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
		if (null == authenticationToken) {
			throw new ApplicationException(ErrorKeys.ACCESS_DENIED);
		}
		return (UserDetails) authenticationToken.getDetails();
	}

	public static String getCurrentUsername() {
		return getCurrentUser().getUsername();
	}

}
