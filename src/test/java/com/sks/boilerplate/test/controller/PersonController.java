package com.sks.boilerplate.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sks.boilerplate.controller.GenericRestController;
import com.sks.boilerplate.test.entity.Person;

@RestController
@RequestMapping(value = "/person")
public class PersonController extends GenericRestController<Person, Long> {

}
