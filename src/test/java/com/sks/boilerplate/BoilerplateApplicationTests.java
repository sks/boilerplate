package com.sks.boilerplate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sks.boilerplate.test.SampleApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleApplication.class)
@WebAppConfiguration
public class BoilerplateApplicationTests {

	@Test
	public void contextLoads() {
	}

}
