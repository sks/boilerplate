package com.sks.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.sks.boilerplate.repository.CustomRepositoryImpl;
import com.sks.boilerplate.test.repository.SampleBaseRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {
		SampleBaseRepository.class }, repositoryBaseClass = CustomRepositoryImpl.class)
public class BoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateApplication.class, args);
	}
}
