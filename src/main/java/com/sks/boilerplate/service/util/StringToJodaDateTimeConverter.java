package com.sks.boilerplate.service.util;

import static org.springframework.util.StringUtils.hasText;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class StringToJodaDateTimeConverter implements Converter<String, DateTime> {

	@Override
	public DateTime convert(String source) {
		if (!hasText(source)) {
			return null;
		}
		return new DateTime(Long.parseLong(source));
	}

}