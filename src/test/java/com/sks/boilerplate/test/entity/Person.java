package com.sks.boilerplate.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sks.boilerplate.annotations.Filterable;
import com.sks.boilerplate.annotations.PartialUpdatable;
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
public class Person extends BaseEntity<Long> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	@NotNull
	@PartialUpdatable
	@NotBlank
	private String name;

	@Column(name = "gender", updatable = false)
	@NotNull
	@Filterable
	@NotBlank
	private String gender;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name = "date_of_birth")
	private DateTime dateOfBirth;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private DateTime bornBefore;

	public Person(String name, String gender) {
		this(name, gender, null);
	}

	public Person(@NonNull String name, @NonNull String gender, DateTime dateOfBirth) {
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
	}

}
