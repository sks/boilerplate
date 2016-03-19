
package com.sks.boilerplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import javax.annotation.Resource;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class WebIntegrationTesting extends SpringBasedTest {

	@Autowired
	protected WebApplicationContext context;

	@Resource
	private FilterChainProxy springSecurityFilterChain;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).addFilter(this.springSecurityFilterChain)
				.defaultRequest(get("/").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.build();
	}

}
