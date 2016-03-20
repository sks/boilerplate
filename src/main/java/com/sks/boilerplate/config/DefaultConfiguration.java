package com.sks.boilerplate.config;

import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.sks.boilerplate.controller.ApplicationExceptionHandler;
import com.sks.boilerplate.service.util.StringToJodaDateTimeConverter;

@Configuration
@EnableJpaAuditing(setDates = true)
public class DefaultConfiguration {

	@Bean
	public ApplicationExceptionHandler exceptionHandler() {
		return new ApplicationExceptionHandler();
	}

	@Bean
	@ConditionalOnClass(value = { DateTime.class })
	public StringToJodaDateTimeConverter stringToJodaDateTimeConverter() {
		return new StringToJodaDateTimeConverter();
	}
}
