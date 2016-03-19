package com.sks.boilerplate.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.sks.boilerplate.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "sample_entity")
@Data
@EqualsAndHashCode(callSuper = false)
public class SampleBaseEntity extends BaseEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

}
