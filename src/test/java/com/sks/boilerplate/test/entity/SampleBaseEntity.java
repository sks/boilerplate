package com.sks.boilerplate.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.sks.boilerplate.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "sample_entity")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SampleBaseEntity extends BaseEntity<Long> {

	public SampleBaseEntity(@NonNull String name) {
		this.name = name;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	@NotNull
	@NotBlank
	private String name;

}
