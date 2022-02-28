/*
 * File Name            :    NisErrorCode.java com.nis.data.utill
 * Project Title        :    nis-common-service
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Jun 4, 2021
 *
 */
package com.ncr.data.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NisErrorCode.java
 */
@AllArgsConstructor
@Getter
public enum NisErrorCode {
	ERROR_PARAMETER_EMPTY("PARAMETER_EMPTY"),

	ERROR_ACCESS_DENIED("ACCESS_DENIED"),

	ERROR_INVALID_UUID("INVALID_UUID"),

	ERROR_NOT_FOUND("NOT_FOUND"),

	ERROR_INVALID("INVALID"),

	ERROR("ERROR");

	String value;
}
