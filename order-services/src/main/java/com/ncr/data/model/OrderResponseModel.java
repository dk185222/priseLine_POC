/*
 * File Name            :    OrderResponseModel.java com.ncr.data.model
 * Project Title        :    order-services
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Feb 25, 2022
 *
 */
package com.ncr.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderResponseModel.java
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseModel {

	private String mobileDeviceId;

	private String orderId;

	private String otherDetails;
}
