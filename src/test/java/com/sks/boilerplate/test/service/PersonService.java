package com.sks.boilerplate.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sks.boilerplate.repository.util.PredicateBuilder;
import com.sks.boilerplate.service.GenericService;
import com.sks.boilerplate.test.entity.Person;
import com.sks.boilerplate.test.repository.PersonPredicateBuilder;
import com.sks.boilerplate.test.repository.PersonRepository;

@Service
public class PersonService extends GenericService<Person, Long> {

	@Autowired
	private PersonRepository personRepository;

	@Override
	public boolean exists(Person entity) {
		return this.personRepository.existsByNameIgnoreCase(entity.getName());
	}

	@Override
	public boolean hasAccessTo(Long id) {
		return this.personRepository.exists(id);
	}

	@Override
	public PredicateBuilder<Person> getPredicate() {
		return new PersonPredicateBuilder();
	}
}