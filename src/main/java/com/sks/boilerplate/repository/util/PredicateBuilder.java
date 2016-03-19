package com.sks.boilerplate.repository.util;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface PredicateBuilder<T> {

	List<Predicate> create(T t, CriteriaBuilder criteriaBuilder, Root<T> root);
}
