package com.sks.boilerplate.entity;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sks.boilerplate.service.CommonService;

import lombok.Getter;

@MappedSuperclass
public class SecuredEntity<ID extends Serializable> extends BaseEntity<ID> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@JoinColumn(name = "modified_by", nullable = false)
	@ManyToOne
	@Getter
	@JsonIgnore
	private String lastModifiedBy;

	@JoinColumn(name = "created_by", nullable = false, updatable = false)
	@ManyToOne
	@Getter
	@JsonIgnore
	private String createdBy;

	@PrePersist
	public void prePersist() {
		this.preUpdate();
		this.createdBy = CommonService.getCurrentUsername();
	}

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedBy = CommonService.getCurrentUsername();
	}
}
