/*
 * File Name            :    ApplicationException.java com.nis.data.error
 * Project Title        :    nis-identity-service-impl
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Jun 3, 2021
 *
 */
package com.ncr.data.error;

import java.io.Serializable;
import java.util.List;

import com.ncr.data.util.NisUtill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.Value;

@Builder
@AllArgsConstructor
@Data
@ToString
@Value
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 8755695716519353114L;

	@Builder.Default
	private String timestamp = NisUtill.getCurrentUTCString();

	@Builder.Default
	private Long longTime = NisUtill.getCurrectTimeInLong();

	private String message;

	private String description;

	private String errorCode;

	private List<Object> paramList;

}
