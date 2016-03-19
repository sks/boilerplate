package com.sks.boilerplate.service.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

public class NullAwareBeanUtil extends BeanUtilsBean {

	static NullAwareBeanUtil instance = new NullAwareBeanUtil();

	public static NullAwareBeanUtil getInstance() {
		return instance;
	}

	private final UpdateablePropertyUtilsBean propertyUtilsBean = new UpdateablePropertyUtilsBean();

	@Override
	public PropertyUtilsBean getPropertyUtils() {
		return this.propertyUtilsBean;
	}

	@Override
	public void copyProperty(Object dest, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		if (value == null) {
			return;
		}
		super.copyProperty(dest, name, value);
	}
}
