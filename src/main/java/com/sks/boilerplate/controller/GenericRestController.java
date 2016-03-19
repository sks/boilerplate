package com.sks.boilerplate.controller;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sks.boilerplate.service.GenericService;

import ch.qos.logback.classic.Logger;

@PreAuthorize(value = "isAuthenticated()")
public class GenericRestController<Entity extends Persistable<ID>, ID extends Serializable> {

	Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected GenericService<Entity, ID> service;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public Entity findById(@PathVariable ID id) {
		return this.service.findById(id);
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<Entity> filter(@ModelAttribute Entity entity) {
		return this.service.filter(entity);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable ID id) {
		this.service.delete(id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public Entity patch(@PathVariable ID id, @RequestBody Entity t) {
		return this.service.patch(id, t);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Entity> create(@Valid @RequestBody Entity entity, HttpServletRequest request) {
		final Entity savedEntity = this.service.save(entity);
		return this.createResponseEntityWithLocatioheader(request, savedEntity, HttpStatus.CREATED);
	}

	protected ResponseEntity<Entity> createResponseEntityWithLocatioheader(HttpServletRequest request,
			final Entity savedEntity, HttpStatus statusCode) {
		final HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.LOCATION, generateURI(request, savedEntity.getId()));
		return new ResponseEntity<Entity>(savedEntity, headers, statusCode);
	}

	protected static String generateURI(HttpServletRequest request, final Object id) {
		return request.getRequestURL().toString() + "/" + id;
	}

}
