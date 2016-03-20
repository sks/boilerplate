package com.sks.boilerplate.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.sks.boilerplate.repository.CustomRepositoryImpl;
import com.sks.boilerplate.test.repository.PersonRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = PersonRepository.class, repositoryBaseClass = CustomRepositoryImpl.class)
public class SampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}
}
