package com.sks.boilerplate.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
public class PageAbleAbstractPersistable<ID extends Serializable> extends AbstractPersistable<ID> {

	private static final long serialVersionUID = 1L;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private Integer page = 1;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private int pageSize = 100;

	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private boolean asc = true;

	public void setPage(int page) {
		if (page < 1) {
			throw new IllegalArgumentException("Page should be greater than 1");
		}
		this.page = page;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.domain.Pageable#getOffset()
	 */
	@JsonIgnore
	public int getOffset() {
		return (this.page - 1) * this.pageSize;
	}

	@JsonProperty("desc")
	public void setDesc(boolean desc) {
		this.asc = !desc;
	}

}
