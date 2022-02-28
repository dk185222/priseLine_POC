/*
 * File Name            :    NisMessages.java com.nis.data.utill
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
 * NisMessages.java
 */
@AllArgsConstructor
@Getter
public enum NisMessages {

	PARAMETER_EMPRY("{0} : cannot be null or empty"),

	PARAMETER_INVALIED("{0} : is in valied"),

	ACCESS_DENIED("{0} is invalied / expired"),

	FILED_IS_EMPTY("filed is empty"),

	COMMON_ERROR("Eror"),

	INVALID_UUID("Invalid UUID passed : %s"),

	NOT_FOUND("parmaneters not found"),

	URL_OR_METHOD_REQUIRED("url or method type cannot empty/null"),

	GET_METHOD_EMPTY_BODY("only GET method can have empty body"),

	INVALID("Invalid value passed")

	;

	String value;
}
