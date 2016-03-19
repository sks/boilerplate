package com.sks.boilerplate.service.util;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.LoggerFactory;

import com.sks.boilerplate.annotations.PartialUpdatable;

import ch.qos.logback.classic.Logger;

public class UpdateablePropertyUtilsBean extends PropertyUtilsBean {

	private static Logger logger = (Logger) LoggerFactory.getLogger(UpdateablePropertyUtilsBean.class);

	@Override
	public boolean isReadable(Object bean, String name) {
		if (!super.isReadable(bean, name)) {
			return false;
		}
		return this.checkIfFieldIsAnnotated(bean, name);

	}

	private boolean checkIfFieldIsAnnotated(Object bean, String name) {
		Field field;
		try {
			field = bean.getClass().getDeclaredField(name);
			return field.getDeclaredAnnotation(PartialUpdatable.class) != null;
		} catch (NoSuchFieldException | SecurityException e) {
			logger.trace("Error getting the field " + name + " from the class " + bean.getClass() + " : Exception "
					+ e.getMessage());
			return false;
		}

	}
}
