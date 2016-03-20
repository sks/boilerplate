package com.sks.boilerplate.repository;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.sks.boilerplate.repository.util.PredicateBuilder;

@NoRepositoryBean
public interface CustomRepository<T extends Persistable<ID>, ID extends Serializable>
		extends PagingAndSortingRepository<T, ID>

{

	/**
	 * Filter.
	 *
	 * @param t
	 *            the object by which the table has to be filtered
	 * @return the iterable objects matching the input object
	 */
	List<T> filter(T t);

	@Modifying
	@Transactional
	void updateField(ID id, String field, Object newValue);

	void setPredicateBuilder(PredicateBuilder<T> predicate);

}