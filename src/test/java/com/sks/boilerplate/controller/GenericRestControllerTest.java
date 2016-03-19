package com.sks.boilerplate.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.function.Function;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;
import com.sks.boilerplate.WebIntegrationTesting;
import com.sks.boilerplate.test.entity.SampleBaseEntity;
import com.sks.boilerplate.test.repository.SampleBaseRepository;

@WebAppConfiguration
public class GenericRestControllerTest extends WebIntegrationTesting {

	@Autowired
	private SampleBaseRepository sampleBaseRepository;

	@Test
	public void whenPostingDataThenReturnCreated() throws Exception {
		this.mockMvc.perform(post("/sample").with(user("user").password("password")).with(csrf())
				.content("{\"name\": \"some_name\"}")).andExpect(status().isCreated());
		List<SampleBaseEntity> allEntities = Lists.newArrayList(this.sampleBaseRepository.findAll());
		assertEquals(1, allEntities.size());
		assertEquals("some_name", allEntities.get(0).getName());
		assertNotNull(allEntities.get(0).getCreateDate());
		assertNotNull(allEntities.get(0).getModifyDate());
	}

	@Test
	public void whenCreatingDuplicateEntityThenReturnConflict() throws Exception {
		this.sampleBaseRepository.save(getSampleEntity(e -> e));
		this.mockMvc
				.perform(post("/sample").with(user("user").password("password")).with(csrf())
						.content("{\"name\": \"name\"}"))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.logref").exists())
				.andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.links").exists())
				.andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.name").value("name"));

		// With different case
		this.mockMvc.perform(
				post("/sample").with(user("user").password("password")).with(csrf()).content("{\"name\": \"NAME\"}"))
				.andExpect(status().isConflict());
		List<SampleBaseEntity> allEntities = Lists.newArrayList(this.sampleBaseRepository.findAll());
		assertEquals(1, allEntities.size());
	}

	@Test
	public void whenGettingNonExisingEntityThenReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/sample/123").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenGettingExistingEntityThenReturnEntity() throws Exception {
		SampleBaseEntity entity = this.sampleBaseRepository.save(getSampleEntity(e -> e));
		this.mockMvc.perform(get("/sample/" + entity.getId()).with(user("user").password("password")).with(csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("name"))
				.andExpect(jsonPath("$.createDate").isNumber()).andExpect(jsonPath("$.modifyDate").isNumber());
	}

	@Test
	public void whenDeletingExistingEntityThenReturnEntity() throws Exception {
		SampleBaseEntity entity = this.sampleBaseRepository.save(getSampleEntity(e -> e));
		this.mockMvc.perform(delete("/sample/" + entity.getId()).with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNoContent());
		List<SampleBaseEntity> allEntities = Lists.newArrayList(this.sampleBaseRepository.findAll());
		assertEquals(0, allEntities.size());
	}

	@Test
	public void whenDeletingNonExistingEntityThenReturnNotFound() throws Exception {
		this.mockMvc.perform(delete("/sample/123").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNotFound());
	}

	private static SampleBaseEntity getSampleEntity(Function<SampleBaseEntity, SampleBaseEntity> mutator) {
		SampleBaseEntity sampleEntity = new SampleBaseEntity("name");
		return mutator.apply(sampleEntity);
	}

	@Test
	public void whenCreatingEntityWithoutValidNameThenReturnBadRequest() throws Exception {
		this.mockMvc.perform(post("/sample").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isBadRequest());

		this.mockMvc.perform(
				post("/sample").with(user("user").password("password")).with(csrf()).content("{\"name\": \"\"}"))
				.andExpect(status().isBadRequest());
	}
}
