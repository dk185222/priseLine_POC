/*
 * File Name            :    NcrAuthorization.java com.nis.data.utill
 * Project Title        :    ncr-catalog-services
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Jun 25, 2021
 *
 */
package com.ncr.data.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ncr.data.error.ApplicationException;
import com.ncr.data.error.ErrorMessage;

/**
 * NcrApiCall.java
 */
@Service
public class NcrApiCall {

	@Value("${ncr.keys.sharedKey}")
	private String sharedKey;

	@Value("${ncr.keys.secretKey}")
	private String secretKey;

	@Value("${ncr.keys.organization}")
	private String organization;

	@Value("${ncr.keys.nepEnterpriseUnit}")
	private String nepEnterpriseUnit;

	private String contentType = "application/json";

	private static final String HMAC = "HmacSHA512";

	@Autowired
	NisRestTemplate nisRestTemplate;

	/**
	 * this is the common call for all the methods
	 *
	 * @param url    : pass the full url ex :
	 *               https://gateway-staging.ncrcloud.com/catalog/items
	 * @param method : HttpMethod type like HttpMethod.GET,HttpMethodPOST etc
	 * @param body   : if any body need to be passed pass in string json formate
	 * @return
	 */
	public String ncrCloudApiCall(String url, HttpMethod method, String body) {
		HttpHeaders headers = this.getDefaultHeaders();
		try {
			this.getHttpHeaders(method.toString(), url, headers);
		} catch (InvalidKeyException e) {
			throw new ApplicationException(
					this.getErrorMessage("Invalied API key passed :InvalidKeyException ", e.getMessage(), "AccessKey"),
					HttpStatus.BAD_REQUEST);
		} catch (MalformedURLException e) {
			throw new ApplicationException(
					this.getErrorMessage("Url need to be in formate http://url.com/api :MalformedURLException",
							e.getMessage(), "Url"),
					HttpStatus.BAD_REQUEST);
		} catch (NoSuchAlgorithmException e) {
			throw new ApplicationException(
					this.getErrorMessage("HmacSHA512 Algorithm fail : NoSuchAlgorithmException", e.getMessage(), HMAC),
					HttpStatus.BAD_REQUEST);
		}

		return nisRestTemplate.restCall(url, body, headers, method);
	}

	private List<ErrorMessage> getErrorMessage(String message, String error, String param) {
		List<ErrorMessage> errorModels = new ArrayList<>();
		List<Object> paramList = new ArrayList<>();
		paramList.add(param);
		errorModels.add(ErrorMessage.builder().message(message).description(error).paramList(paramList)
				.errorCode(NisErrorCode.ERROR.getValue()).build());
		return errorModels;
	}

	/**
	 * @param method
	 * @param uri
	 * @param headers
	 * @return
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private HttpHeaders getHttpHeaders(String method, String uri, HttpHeaders headers)
			throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {

		URL url = new URL(uri);

		Date dateNow = new Date();
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		SimpleDateFormat gmtDateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		gmtDateFormatISO.setTimeZone(TimeZone.getTimeZone("GMT"));

		String dateString = gmtDateFormat.format(dateNow);
		String dateStringISO = gmtDateFormatISO.format(dateNow);

		String secretKeyWithTime = secretKey.concat(dateStringISO);

		String textToSign = this.joinString(method, url.getPath(), url.getQuery());

		SecretKeySpec keySpec = new SecretKeySpec(secretKeyWithTime.getBytes(StandardCharsets.UTF_8), HMAC);
		Mac mac = Mac.getInstance(HMAC);
		mac.init(keySpec);

		byte[] signed = mac.doFinal(textToSign.getBytes(StandardCharsets.UTF_8));
		String accessKey = sharedKey.concat(":").concat(Base64.getEncoder().encodeToString(signed));

		headers.add("nep-organization", organization);
		headers.add("Date", dateString);
		headers.add("nep-enterprise-unit", nepEnterpriseUnit);
		headers.add("Authorization", "AccessKey ".concat(accessKey));

		return headers;
	}

	/**
	 * @return HttpHeaders object
	 */
	private HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", contentType);
		headers.add("Accept", "*/*");
		return headers;
	}

	/**
	 * @param method : GET,POST etc
	 * @param url    : /catalog/items
	 * @return : string built with \n
	 */
	private String joinString(String method, String url, String queryParm) {
		StringJoiner j = new StringJoiner("\n");
		j.add(method);
		if (!NisUtill.isEmpty(queryParm)) {
			url = url.concat("?").concat(queryParm);
		}
		j.add(url);
		j.add(contentType);
		j.add(organization);
		return j.toString();
	}

}
