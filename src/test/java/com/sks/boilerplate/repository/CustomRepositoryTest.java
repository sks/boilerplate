package com.sks.boilerplate.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sks.boilerplate.SpringBasedTest;
import com.sks.boilerplate.test.entity.Person;
import com.sks.boilerplate.test.repository.PersonRepository;

public class CustomRepositoryTest extends SpringBasedTest {

	@Autowired
	private PersonRepository personRepository;

	@Test
	public void whenUpdatingTheFieldThenUpdateTheField() {
		Person person = this.personRepository.save(getPerson("name"));
		this.personRepository.updateField(person.getId(), "name", "updated_name");
		Person updatedPerson = this.personRepository.findOne(person.getId());
		assertEquals("updated_name", updatedPerson.getName());
	}
}
