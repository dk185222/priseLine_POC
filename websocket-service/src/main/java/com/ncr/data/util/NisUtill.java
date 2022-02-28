package com.ncr.data.util;
/*
 * File Name            :    NisUtill.java com.nis.data.utill
 * Project Title        :    nis-identity-service-impl
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Jun 4, 2021
 *
 */

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.ncr.data.error.ApplicationException;
import com.ncr.data.error.ErrorMessage;

/**
 * NisUtill.java
 */
public class NisUtill {

	private static final String UDATE_FORMAT = "MM/dd/yyyy hh:mm:ss a";

	private static final String REPONSE_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	/**
	 * Cast an Object to UUID.
	 *
	 * @param <T>   the generic type
	 * @param clzz  - the type T
	 * @param value - The object value.
	 * @return - the type T.
	 * @throws ApplicationException
	 */
	public static UUID toUUID(Object value) throws ApplicationException {

		UUID uuid = null;
		if (value != null) {
			try {
				uuid = UUID.fromString(value.toString());
			} catch (IllegalArgumentException e) {
				List<ErrorMessage> errorModels = new ArrayList<>();
				errorModels.add(ErrorMessage.builder().message(NisMessages.FILED_IS_EMPTY.getValue())
						.description(String.format(NisMessages.INVALID_UUID.getValue(), value))
						.errorCode(NisErrorCode.ERROR_INVALID_UUID.getValue()).build());

				throw new ApplicationException(errorModels, HttpStatus.EXPECTATION_FAILED);
			}
		}

		return uuid;
	}

	public static UUID generateTimeBasedUUID() {
		return UUID.nameUUIDFromBytes("NCR".concat(getCurrectTimeInLong().toString()).getBytes());
	}

	public static Long getCurrectTimeInLong() {
		return getCurrentTime().getTime() / 1000;
	}

	public static Long getTimeInLong(Date date) {
		return date.getTime() / 1000;
	}

	public static Date getCurrentTime() {
		OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
		return Date.from(utc.toInstant());
	}

	public static String getCurrentUTCString() {
		OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
		return Date.from(utc.toInstant()).toString();
	}

	public static String convertToUserZoneTime(String datestring, String userZone) {

		LocalDateTime ldt = null;
		try {
			ldt = LocalDateTime.parse(datestring, DateTimeFormatter.ofPattern(UDATE_FORMAT));
		} catch (DateTimeParseException e) {
			return datestring;
		}

		ZoneId utcZoneId = ZoneId.of(ZoneOffset.UTC.getId());
		ZonedDateTime utcDateTime = ldt.atZone(utcZoneId);

		ZoneId userZoneId = null;
		try {
			userZoneId = ZoneId.of(userZone);
		} catch (DateTimeException e) {
			return datestring;
		}

		ZonedDateTime userDateTime = utcDateTime.withZoneSameInstant(userZoneId);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(REPONSE_DATE_FORMAT);

		return format.format(userDateTime);
	}

	public static String getGmtDateFormatISO(Date date) {
		SimpleDateFormat gmtDateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		gmtDateFormatISO.setTimeZone(TimeZone.getTimeZone("GMT"));

		return gmtDateFormatISO.format(date);
	}

	public static String getGmtDateFormat(Date date) {
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return gmtDateFormat.format(date);
	}

	public static String convertToUserZoneTime(Date date, String userZone) {
		if (!NisUtill.isEmpty(date)) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			SimpleDateFormat sdf = new SimpleDateFormat(REPONSE_DATE_FORMAT);

			if (NisUtill.isEmpty(userZone)) {
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				return sdf.format(calendar.getTime());
			}

			sdf.setTimeZone(TimeZone.getTimeZone(userZone));
			return sdf.format(calendar.getTime());
		}
		return null;
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value - the String
	 * @return the boolean
	 * @return- true if the string is empty or null.
	 */
	public static final boolean isEmpty(String value) {
		return null == value || value.trim().isEmpty();
	}

	public static final boolean isEmpty(UUID value) {
		return null == value;
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value        - the String
	 * @param defaultValue the default value
	 * @return the boolean
	 * @return- true if the string is empty or null.
	 */
	public static String isEmpty(String value, String defaultValue) {
		if (null == value || value.trim().isEmpty()) {
			value = defaultValue;
		}
		return value;
	}

	/**
	 * Checks if is empty.
	 *
	 * @param <T>  the generic type
	 * @param list the list
	 * @return the boolean
	 */
	public static <T> boolean isEmpty(List<T> list) {
		return null == list || list.isEmpty();
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value - the Object
	 * @return the boolean
	 * @return- true if the string is empty or null.
	 */
	public static boolean isEmpty(Object value) {
		return null == value;
	}

	/**
	 * Checks if the value is not null and not empty, if the passed value is null or
	 * empty then it will return the default value.
	 *
	 * @param value        the value
	 * @param defaultValue the default value
	 * @return the string value
	 */
	public static Object getObjectValue(Object value, Object defaultValue) {
		Object returnValue = defaultValue;

		if (value != null) {
			returnValue = value;
		}

		return returnValue;
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value - Integer
	 * @return the boolean
	 * @return- true if the Integer is empty or null.
	 */
	public static boolean isEmpty(Integer value) {
		return value == null;
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value - Long
	 * @return the boolean
	 * @return- true if the Long is empty or null.
	 */
	public static boolean isEmpty(Long value) {
		return value == null;
	}

	/**
	 * Validates whether the value is empty or not .
	 *
	 * @param value - BigInteger
	 * @return the boolean
	 * @return- true if the BigInteger is empty or null.
	 */
	public static Boolean isEmpty(BigInteger value) {
		return value == null;
	}

}
