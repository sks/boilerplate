package com.sks.boilerplate.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sks.boilerplate.service.GenericService;
import com.sks.boilerplate.test.entity.SampleBaseEntity;
import com.sks.boilerplate.test.repository.SampleBaseRepository;

@Service
public class SampleBaseService extends GenericService<SampleBaseEntity, Long> {

	@Autowired
	private SampleBaseRepository sampleBaseRepository;

	@Override
	public boolean exists(SampleBaseEntity entity) {
		return this.sampleBaseRepository.existsByNameIgnoreCase(entity.getName());
	}

	@Override
	public boolean hasAccessTo(Long id) {
		return this.repository.exists(id);
	}

}
