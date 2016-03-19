package com.sks.boilerplate.test.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sks.boilerplate.repository.util.PredicateBuilder;
import com.sks.boilerplate.test.entity.Person;

public class PersonPredicateBuilder implements PredicateBuilder<Person> {

	@Override
	public List<Predicate> create(Person person, CriteriaBuilder criteriaBuilder, Root<Person> root) {
		final List<Predicate> predicateList = new ArrayList<>();

		if (null != person.getBornBefore()) {
			predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), person.getBornBefore()));
		}
		return predicateList;
	}

}
