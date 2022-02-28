/*
 * File Name            :    OrderRepo.java com.ncr.data.repo
 * Project Title        :    app-hive
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Feb 22, 2022
 *
 */
package com.ncr.data.repo;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ncr.data.model.Order;

/**
 * OrderRepo.java
 */
@Repository
@Configurable
public interface OrderRepo extends MongoRepository<Order, Long> {

	public Order findByOrderIdAndStatus(String orderId, String status);

}
