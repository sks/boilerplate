package com.sks.boilerplate.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.sks.boilerplate.config.DefaultConfiguration;
import com.sks.boilerplate.repository.CustomRepositoryImpl;
import com.sks.boilerplate.test.repository.PersonRepository;

@SpringBootApplication
@Import(value = { DefaultConfiguration.class })
@EnableJpaRepositories(basePackageClasses = PersonRepository.class, repositoryBaseClass = CustomRepositoryImpl.class)
public class BoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateApplication.class, args);
	}
}
