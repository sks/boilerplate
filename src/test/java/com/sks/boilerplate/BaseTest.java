package com.sks.boilerplate;

import java.util.function.Function;
import java.util.function.Supplier;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import com.sks.boilerplate.security.UserAuthentication;
import com.sks.boilerplate.test.SampleApplication;
import com.sks.boilerplate.test.entity.Person;

import ch.qos.logback.classic.Logger;

@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = { SampleApplication.class })
public class BaseTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public static Logger logger = (Logger) LoggerFactory.getLogger(BaseTest.class);

	public static <T> T runAsUser(UserDetails user, Supplier<T> function) {
		final UserAuthentication userAuthentication = new UserAuthentication(user);
		// Add the authentication to the Security context
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		T t = function.get();
		SecurityContextHolder.getContext().setAuthentication(null);
		return t;
	}

	protected static Person getPerson(Function<Person, Person> mutator) {
		return getPerson(mutator, "name");
	}

	private static Person getPerson(final Function<Person, Person> mutator, final String name) {
		return mutator.apply(getPerson(name));
	}

	protected static Person getPerson(final String name) {
		return getPerson(name, "F");
	}

	protected static Person getPerson(final String name, final String gender) {
		return new Person(name, gender);
	}

	protected static Person getPerson(final String name, final String gender, final DateTime dateOfBirth) {
		return new Person(name, gender, dateOfBirth);
	}
}
