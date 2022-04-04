/*
 * File Name            :    com.ncr.data.websockets ProcessMessage.java
 * Project Title        :    websocket-service
 * Copyright            :    Copyright (c) 2022 NCR Corporation
 * Author               :    dk185222
 * Date					:	 Apr 4, 2022
 *
 */
package com.ncr.data.websockets;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Date;

import org.bson.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.ncr.data.util.NisUtill;

@Service
public class ProcessMessage {

	private static String host = "153.78.56.201";

	private static String username = "test";

	private static String password = "password";

	private static Integer port = 8000;

	private static String mainTopic = "pricelineordertopic/";

	private static String serverPath = "mqtt";

	private final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
	private final MongoDatabase database = mongoClient.getDatabase("hiveDb");
	private final MongoCollection collection = database.getCollection("order_details");

	@Async
	@Transactional
	public void asyncUpdate(String message) {
		var returVal = updateOrder(message);
		if (returVal != null) {
			postToHiveQueue(returVal);
		}
	}

	private Order updateOrder(String data) {
		try {

			String orderStaus = JsonPath.read(data, "$.message.attributes[4].value");

			if (orderStaus != null
					&& ("CANCELED".equalsIgnoreCase(orderStaus) || "FINISHED".equalsIgnoreCase(orderStaus))) {
				String orderId = JsonPath.read(data, "$.message.attributes[0].value");
				orderId = orderId.split("/")[1];

				String timeResived = NisUtill.getGmtDateFormatISO(new Date());

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
			System.err.println("Path does not exist in JSON : " + exception.getMessage());
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
