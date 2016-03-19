package com.sks.boilerplate.test.repository;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sks.boilerplate.repository.CustomRepository;
import com.sks.boilerplate.test.entity.Person;

@Repository
public interface PersonRepository extends CustomRepository<Person, Long> {

	Person findByNameIgnoreCase(String name);

	@Query("select case when count(e) > 0 then true else false end from Person e where lower(e.name) = lower(?1)")
	boolean existsByNameIgnoreCase(String name);

	Iterable<Person> findByDateOfBirthBefore(DateTime bornBefore);

}
