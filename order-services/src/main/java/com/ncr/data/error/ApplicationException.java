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

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * ApplicationException.java
 */
@Getter
@Setter
public class ApplicationException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 4754526254040645906L;

	private List<ErrorMessage> errorMessages;

	private HttpStatus httpStatu;

	public ApplicationException() {
		super();
	}

	public ApplicationException(List<ErrorMessage> errorMessages, HttpStatus httpStatu) {
		super();
		this.errorMessages = errorMessages;
		this.httpStatu = httpStatu;
	}

}
