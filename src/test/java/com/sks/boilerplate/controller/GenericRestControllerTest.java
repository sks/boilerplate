package com.sks.boilerplate.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;
import com.sks.boilerplate.WebIntegrationTesting;
import com.sks.boilerplate.test.entity.Person;
import com.sks.boilerplate.test.repository.PersonRepository;

@WebAppConfiguration
public class GenericRestControllerTest extends WebIntegrationTesting {

	@Autowired
	private PersonRepository personRepository;

	@Test
	public void whenPostingDataThenReturnCreated() throws Exception {
		this.mockMvc.perform(post("/person").with(user("user").password("password")).with(csrf())
				.content("{\"name\": \"some_name\",\"gender\" : \"F\"}")).andExpect(status().isCreated());
		List<Person> allEntities = Lists.newArrayList(this.personRepository.findAll());
		assertEquals(1, allEntities.size());
		assertEquals("some_name", allEntities.get(0).getName());
		assertEquals("F", allEntities.get(0).getGender());
		assertNotNull(allEntities.get(0).getCreateDate());
		assertNotNull(allEntities.get(0).getModifyDate());
	}

	@Test
	public void whenCreatingDuplicateEntityThenReturnConflict() throws Exception {
		this.createdEntityInDB();
		this.mockMvc
				.perform(post("/person").with(user("user").password("password")).with(csrf())
						.content("{\"name\": \"name\", \"gender\" : \"F\"}"))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.logref").exists())
				.andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.links").exists())
				.andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.name").value("name"));

		// With different case
		this.mockMvc.perform(post("/person").with(user("user").password("password")).with(csrf())
				.content("{\"name\": \"NAME\", \"gender\" : \"F\"}")).andExpect(status().isConflict());
		List<Person> allEntities = Lists.newArrayList(this.personRepository.findAll());
		assertEquals(1, allEntities.size());
	}

	@Test
	public void whenGettingNonExisingEntityThenReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/person/123").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenGettingExistingEntityThenReturnEntity() throws Exception {
		Person entity = this.createdEntityInDB();
		this.mockMvc.perform(get("/person/" + entity.getId()).with(user("user").password("password")).with(csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("name"))
				.andExpect(jsonPath("$.createDate").isNumber()).andExpect(jsonPath("$.modifyDate").isNumber());
	}

	@Test
	public void whenDeletingExistingEntityThenReturnEntity() throws Exception {
		Person entity = this.createdEntityInDB();
		this.mockMvc.perform(delete("/person/" + entity.getId()).with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNoContent());
		List<Person> allEntities = Lists.newArrayList(this.personRepository.findAll());
		assertEquals(0, allEntities.size());
	}

	private Person createdEntityInDB() {
		return this.personRepository.save(getPersonEntity(e -> e));
	}

	@Test
	public void whenDeletingNonExistingEntityThenReturnNotFound() throws Exception {
		this.mockMvc.perform(delete("/person/123").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenGettingAllEntitiesThenReturnList() throws Exception {
		IntStream.rangeClosed(1, 8).mapToObj(i -> getPersonEntity("name_" + i)).forEach(this.personRepository::save);
		this.mockMvc.perform(get("/person").with(user("user").password("password"))).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(8)));
	}

	@Test
	public void whenFilteringBasedOnTypeThenReturnFilteredValues() throws Exception {
		IntStream.rangeClosed(1, 8).mapToObj(i -> getPersonEntity("name_" + i, i % 2 == 0 ? "F" : "M"))
				.forEach(this.personRepository::save);
		this.mockMvc.perform(get("/person").with(user("user").password("password")).param("gender", "F"))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(4)));

		this.mockMvc.perform(get("/person").with(user("user").password("password")).param("gender", "NON_EXISTING"))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void whenUpdatingNonExistingEntityThenReturnNotFound() throws Exception {
		this.mockMvc.perform(
				put("/person/12").content("{\"name\":\"updated_name\"}").with(user("user").password("password")))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenUpdatingNonUpdatableFieldThenDontUpdateThatField() throws Exception {
		Person entity = this.createdEntityInDB();
		this.mockMvc.perform(
				put("/person/" + entity.getId()).content("{\"gender\":\"M\"}").with(user("user").password("password")))
				.andExpect(status().isAccepted());

		Person updatedEntity = this.personRepository.findOne(entity.getId());
		assertEquals("F", updatedEntity.getGender());
	}

	@Test
	public void whenUpdatingParticularFieldThenUpdateTheValue() throws Exception {
		Person entity = this.createdEntityInDB();
		this.mockMvc.perform(put("/person/" + entity.getId()).content("{\"name\":\"updated_name\"}")
				.with(user("user").password("password"))).andExpect(status().isAccepted());

		Person updatedEntity = this.personRepository.findOne(entity.getId());
		assertEquals("updated_name", updatedEntity.getName());
		assertEquals("F", updatedEntity.getGender());
	}

	private static Person getPersonEntity(Function<Person, Person> mutator) {
		return getPersonEntity(mutator, "name");
	}

	private static Person getPersonEntity(Function<Person, Person> mutator, String name) {
		return mutator.apply(getPersonEntity(name));
	}

	private static Person getPersonEntity(String name) {
		return getPersonEntity(name, "F");
	}

	private static Person getPersonEntity(String name, String gender) {
		return new Person(name, gender);
	}

	@Test
	public void whenCreatingEntityWithoutValidNameThenReturnBadRequest() throws Exception {
		this.mockMvc.perform(post("/person").with(user("user").password("password")).with(csrf()))
				.andExpect(status().isBadRequest());

		this.mockMvc.perform(
				post("/person").with(user("user").password("password")).with(csrf()).content("{\"name\": \"\"}"))
				.andExpect(status().isBadRequest());
	}
}
