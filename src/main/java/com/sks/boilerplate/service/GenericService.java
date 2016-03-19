package com.sks.boilerplate.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;

import com.sks.boilerplate.enums.ErrorKeys;
import com.sks.boilerplate.exception.ApplicationException;
import com.sks.boilerplate.repository.CustomRepository;
import com.sks.boilerplate.service.util.NullAwareBeanUtil;

import lombok.NonNull;

public abstract class GenericService<Entity extends Persistable<ID>, ID extends Serializable> extends CommonService {

	@Autowired
	protected CustomRepository<Entity, ID> repository;

	public Entity findById(@NonNull ID id) {
		this.verifyAccess(id);
		return returnIfNotNull(this.repository.findOne(id));
	}

	public Entity save(Entity entity) {
		if (this.exists(entity)) {
			throw new ApplicationException(ErrorKeys.CONFLICTING_RECORD, entity);
		}
		return this.repository.save(entity);
	}

	public abstract boolean exists(Entity entity);

	public abstract boolean hasAccessTo(ID id);

	public void delete(ID id) {
		this.verifyAccess(id);
		this.repository.delete(id);
	}

	protected void verifyAccess(ID id) {
		if (!this.hasAccessTo(id)) {
			throw new EntityNotFoundException();
		}
	}

	public List<Entity> filter(Entity entity) {
		return this.repository.filter(entity);
	}

	public Entity patch(ID id, Entity t) {
		final Entity entity = this.findById(id);

		try {
			NullAwareBeanUtil.getInstance().copyProperties(entity, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			this.logger.error("Error while copying the properties over", e);
			throw new ApplicationException(ErrorKeys.UNPROCESSABLE_ENTITY);
		}
		return this.repository.save(entity);
	}
}
