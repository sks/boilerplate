package com.sks.boilerplate.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sks.boilerplate.controller.GenericRestController;
import com.sks.boilerplate.test.entity.SampleBaseEntity;

@RestController
@RequestMapping(value = "/sample")
public class SampleBaseController extends GenericRestController<SampleBaseEntity, Long> {

}
