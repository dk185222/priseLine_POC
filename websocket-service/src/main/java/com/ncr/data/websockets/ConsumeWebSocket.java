/*
 * File Name            :    ConsumeWebSocket.java com.ncr.data.websockets
 * Project Title        :    websocket-service
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Feb 25, 2022
 *
 */
package com.ncr.data.websockets;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.ncr.data.model.Order;
import com.ncr.data.model.OrderResponseModel;
import com.ncr.data.util.NcrApiCall;
import com.ncr.data.util.NisUtill;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

/**
 * ConsumeWebSocket.java
 */
@Component
public class ConsumeWebSocket {

	@Autowired
	private ProcessMessage processMessage;

	@Autowired
	private NcrApiCall ncrApiCall;

	private static final String SERVER = "wss://notifications-staging.ncrcloud.com/messaging/subscribe?token=";

	@PostConstruct
	public void connectWebSocket() {
		try {
			WebSocket ws = connect();

			String text = "{\r\n" + "  \"subscriptions\": [\r\n" + "    {\r\n" + "      \"topicId\": {\r\n"
					+ "        \"name\": \"order_change_3\"\r\n" + "      },\r\n"
					+ "      \"messageAttributePatterns\": [\r\n" + "        {\r\n"
					+ "          \"key\": \"enterprise-unit-id\",\r\n"
					+ "          \"value\": \"91426f4986084490af3e52981b1000fd\"\r\n" + "        }\r\n" + "      ]\r\n"
					+ "    }\r\n" + "  ]\r\n" + "}\r\n";

			ws.sendText(text);

		} catch (Exception ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		}
	}

	/**
	 * Connect to the server.
	 */
	private WebSocket connect() throws IOException, WebSocketException {
		final var token = this.getToken();
		System.out.println("token : " + token);
		return new WebSocketFactory().createSocket(SERVER.concat(token)).addListener(new WebSocketAdapter() {
			@Override
			public void onTextMessage(WebSocket websocket, String message) {
				System.out.println("message: " + message);
				if (message != null) {
					processMessage.asyncUpdate(message);
				}
				System.out.println("message: ");
			}

		}).addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).connect();
	}

	private String getToken() throws JsonProcessingException {
		ObjectNode node = new ObjectMapper().readValue(
				ncrApiCall.ncrCloudApiCall("https://gateway-staging.ncrcloud.com/security/authentication/login",
						HttpMethod.POST, ""),
				ObjectNode.class);
		return node.get("token").asText();

	}

}
