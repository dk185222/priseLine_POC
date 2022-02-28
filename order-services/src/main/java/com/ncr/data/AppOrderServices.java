/*
 * File Name            :    NcrNisEsbIntigrationServices.java com.ncr.nis
 * Project Title        :    nis-rbs-esb
 * Copyright            :    Copyright (c) 2021 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Sep 7, 2021
 *
 */
package com.ncr.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * NcrNisEsbIntigrationServices.java
 */
@SpringBootApplication
@ComponentScan(basePackages = { "application-base-package", "com.ncr.data" })
public class AppOrderServices {

	public static void main(final String[] args) {
		SpringApplication.run(AppOrderServices.class, args);
	}
}
