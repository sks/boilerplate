package com.sks.boilerplate.repository;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

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
	int updateField(ID id, String field, Object newValue);

}
