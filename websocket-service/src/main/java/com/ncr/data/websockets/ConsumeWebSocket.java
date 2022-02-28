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

	private static String host = "153.78.56.201";

	private static String username = "test";

	private static String password = "password";

	private static Integer port = 8000;

	private static String mainTopic = "pricelineordertopic/";

	private static String serverPath = "mqtt";

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
		return new WebSocketFactory().createSocket(SERVER.concat(token)).addListener(new WebSocketAdapter() {
			@Override
			public void onTextMessage(WebSocket websocket, String message) {
				if (message != null) {
					var returVal = updateOrder(message);
					if (returVal != null) {
						postToHiveQueue(returVal);
					}
				}
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

	private Order updateOrder(String data) {
		try {
			String orderStaus = JsonPath.read(data, "$.message.map.attributes[4].value");

			if (orderStaus != null
					&& ("CANCELED".equalsIgnoreCase(orderStaus) || "FINISHED".equalsIgnoreCase(orderStaus))) {
				String orderId = JsonPath.read(data, "$.message.map.attributes[0].value");
				orderId = orderId.split("/")[1];

				String timeResived = NisUtill.getGmtDateFormatISO(new Date());
				final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
				final MongoDatabase database = mongoClient.getDatabase("hiveDb");
				MongoCollection collection = database.getCollection("order_details");

				Document o = (Document) collection
						.find(Filters.and(Filters.eq("status", "OrderPlaced"), Filters.eq("orderId", orderId))).first();

				String postHive = NisUtill.getGmtDateFormatISO(new Date());

				collection.updateMany(Filters.eq("_id", o.get("_id")),
						Updates.combine(Updates.set("status", "completed"),
								Updates.set("timeOrderResivedFromBsl", timeResived),
								Updates.set("timeOrderPosteToHive", postHive)));

				return Order.builder().mobileDeviceId(o.get("mobileDeviceId").toString()).orderId(orderId)
						.status("completed").otherDetails(data).build();
			} else {
				System.out.println("Skip record as the order is not CANCELED | FINISHED");
			}
		} catch (Exception exception) {
			System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
		}

		return null;
	}

	private void postToHiveQueue(Order order) {
		if (order != null) {
			try {

				final Mqtt5BlockingClient client = MqttClient.builder().useMqttVersion5().serverHost(host)
						.serverPort(port).webSocketConfig().serverPath(serverPath).applyWebSocketConfig()
						.buildBlocking();

				var topic = mainTopic.concat(order.getMobileDeviceId());

				client.connectWith().simpleAuth().username(username).password(UTF_8.encode(password)).applySimpleAuth()
						.send();

				client.subscribeWith().topicFilter(topic).qos(MqttQos.EXACTLY_ONCE).send();
				OrderResponseModel o = OrderResponseModel.builder().mobileDeviceId(order.getMobileDeviceId())
						.orderId(order.getOrderId()).otherDetails(order.getOtherDetails()).build();

				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(o);

				client.publishWith().topic(topic).payload(UTF_8.encode(json)).qos(MqttQos.EXACTLY_ONCE).send();

			} catch (Exception exception) {
				System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
			}
		}
	}

}
