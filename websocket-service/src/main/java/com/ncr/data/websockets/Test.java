/*
 * File Name            :    Test.java com.ncr.data.websockets
 * Project Title        :    websocket-service
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Feb 24, 2022
 *
 */
package com.ncr.data.websockets;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

/**
 * Test.java
 */
public class Test {
	public static void main(String[] args) {
		final String host = "153.78.56.201";
		final String username = "test";
		final String password = "password";

		final Mqtt5BlockingClient client = MqttClient.builder().useMqttVersion5().serverHost(host).serverPort(8000)
				.webSocketConfig().serverPath("mqtt").applyWebSocketConfig().buildBlocking();

		client.connectWith().simpleAuth().username(username).password(UTF_8.encode(password)).applySimpleAuth().send();

		client.subscribeWith().topicFilter("pricelineordertopic/#").send();

		client.toAsync().publishes(MqttGlobalPublishFilter.ALL, publish ->

		// Here u will write a if logic to match the token and if token match u will
		// update u r local db

		System.out.println("Received message Test class: " + publish.getTopic() + " -> "
				+ UTF_8.decode(publish.getPayload().get()))

		);

	}

}
