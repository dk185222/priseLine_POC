/*
 * File Name            :    Order.java com.ncr.data.model
 * Project Title        :    app-hive
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Feb 22, 2022
 *
 */
package com.ncr.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order.java
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_details")
public class Order {

	@Id
	private String id;

	private String mobileDeviceId;

	private String orderId;

	private String status;

	private String otherDetails;

}
