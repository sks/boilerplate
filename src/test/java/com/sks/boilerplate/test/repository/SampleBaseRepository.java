package com.sks.boilerplate.test.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sks.boilerplate.repository.CustomRepository;
import com.sks.boilerplate.test.entity.SampleBaseEntity;

@Repository
public interface SampleBaseRepository extends CustomRepository<SampleBaseEntity, Long> {

	SampleBaseEntity findByNameIgnoreCase(String name);

	@Query("select case when count(e) > 0 then true else false end from SampleBaseEntity e where lower(e.name) = lower(?1)")
	boolean existsByNameIgnoreCase(String name);

}
