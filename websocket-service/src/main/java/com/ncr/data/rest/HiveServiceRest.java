/*
 * File Name            :    IdentityServiceRest.java com.ncr.data.rest
 * Project Title        :    nis-identity-service-impl
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    rg185129
 * Date					:	 Sep 1, 2021
 *
 */
package com.ncr.data.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncr.data.model.Order;
import com.ncr.data.repo.OrderRepo;

/**
 * IdentityServiceRest.java
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HiveServiceRest {

	@Autowired
	private OrderRepo orderRepo;

	@PostMapping(value = "/submit-order")
	public ResponseEntity<String> submitOrder(@RequestBody(required = true) Order order) throws Exception {
		orderRepo.save(order);
		return new ResponseEntity<>("Data Submited", HttpStatus.OK);
	}

	@PostMapping(value = "/post-to-hive")
	public ResponseEntity<String> postToHive(@RequestBody(required = true) Order order) throws Exception {
		// hiveServices.postToHiveQueue(order);
		return new ResponseEntity<>("Data Sent", HttpStatus.OK);
	}

	@GetMapping(value = "/get-last-order")
	public ResponseEntity<Order> getlogo(@RequestHeader Map<String, String> headers,
			@RequestParam(required = true) String mobileDeviceId) throws Exception {
		var o = Order.builder().build();
		return new ResponseEntity<Order>(o, HttpStatus.OK);
	}

}
