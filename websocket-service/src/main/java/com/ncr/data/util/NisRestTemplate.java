/*
 * File Name            :    NisRestTemplate.java com.nis.data.utill
 * Project Title        :    ncr-common-service
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Jun 25, 2021
 *
 */
package com.ncr.data.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ncr.data.error.ApplicationException;
import com.ncr.data.error.ErrorMessage;

/**
 * NisRestTemplate.java
 */
@Service
public class NisRestTemplate {
	private RestTemplate rest;
	private HttpHeaders headers;
	private HttpStatus status;
	private String body = "";

	/**
	 * @param headers
	 * @param body
	 */
	private void restTemplateInit(HttpHeaders headers, String body) {
		this.rest = new RestTemplate();
		if (headers == null) {
			this.headers = new HttpHeaders();
			this.headers.add("Content-Type", "application/json");
			this.headers.add("Accept", "*/*");
		} else {
			this.headers = headers;
		}
		if (!NisUtill.isEmpty(body)) {
			this.body = body;
		}
	}

	/**
	 * This is the common rest call using spring RestTemplate
	 *
	 * @param uri     - String value like "https://httpbin.org/get" cannot be null
	 * @param body    - can be null for GET method only
	 * @param headers - can be null but will take default values
	 * @param method  - cannot be null
	 * @return string for any method call
	 */
	public String restCall(String uri, String body, HttpHeaders headers, HttpMethod method) {
		validateRequest(uri, headers, body, method);
		HttpEntity<String> requestEntity = new HttpEntity<>(this.body, this.headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, method, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());
		return responseEntity.getBody();
	}

	/**
	 * @param uri
	 * @param headers
	 * @param body
	 * @param method
	 */
	private void validateRequest(String uri, HttpHeaders headers, String body, HttpMethod method) {
		if (NisUtill.isEmpty(uri) || method == null) {
			throw new ApplicationException(this.getError(), HttpStatus.BAD_REQUEST);
		}
		/**
		 * if (!method.equals(HttpMethod.GET) && NisUtill.isEmpty(body)) { throw new
		 * ApplicationException(this.getMethodError(method), HttpStatus.BAD_REQUEST); }
		 */

		this.restTemplateInit(headers, body);
	}

	/**
	 * @return list of error messages
	 */
	private List<ErrorMessage> getError() {
		List<ErrorMessage> errorModels = new ArrayList<>();
		List<Object> paramList = new ArrayList<>();
		paramList.add("url / method");
		errorModels.add(ErrorMessage.builder().message(NisMessages.URL_OR_METHOD_REQUIRED.getValue())
				.description(NisMessages.URL_OR_METHOD_REQUIRED.getValue()).paramList(paramList)
				.errorCode(NisErrorCode.ERROR_PARAMETER_EMPTY.getValue()).build());

		return errorModels;
	}

	/**
	 * @return list of error messages
	 */
	private List<ErrorMessage> getMethodError(HttpMethod method) {
		List<ErrorMessage> errorModels = new ArrayList<>();
		List<Object> paramList = new ArrayList<>();
		paramList.add(method);
		errorModels.add(ErrorMessage.builder().message(NisMessages.GET_METHOD_EMPTY_BODY.getValue())
				.description(NisMessages.GET_METHOD_EMPTY_BODY.getValue()).paramList(paramList)
				.errorCode(NisErrorCode.ERROR_INVALID.getValue()).build());

		return errorModels;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}
