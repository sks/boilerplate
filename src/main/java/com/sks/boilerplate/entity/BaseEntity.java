package com.sks.boilerplate.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(value = Include.NON_NULL)
public abstract class BaseEntity<ID extends Serializable> extends PageAbleAbstractPersistable<ID> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@LastModifiedDate
	@Column(name = "last_modified_timestamp")
	@Version
	@Getter
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime modifyDate;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@CreatedDate
	@Column(name = "created_timestamp", nullable = false, updatable = false)
	@Getter
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime createDate;

	@Override
	@JsonIgnore
	public boolean isNew() {
		return super.isNew();
	}
}
