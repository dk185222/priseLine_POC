/*
 * File Name            :    IdentityServiceRest.java com.ncr.data.rest
 * Project Title        :    nis-identity-service-impl
 * Copyright            :    Copyright (c) 2018-2019 NCR Corporation
 * Author               :    rg185129
 * Date					:	 Sep 1, 2021
 *
 */
package com.ncr.data.rest;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ncr.data.model.Order;
import com.ncr.data.model.OrderResponseModel;
import com.ncr.data.repo.OrderRepo;
import com.ncr.data.util.NcrApiCall;
import com.ncr.data.util.NisUtill;

/**
 * IdentityServiceRest.java
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderServices {

	@Autowired
	private NcrApiCall ncrApiCall;

	@Autowired
	private OrderRepo orderRepo;

	@PostMapping(value = "/submit-order")
	public ResponseEntity<OrderResponseModel> submitOrder(@RequestBody(required = true) Order order) throws Exception {
		String body = this.getBody();
		// submit to BSL
		var resData = ncrApiCall.ncrCloudApiCall("https://gateway-staging.ncrcloud.com/order/orders", HttpMethod.POST,
				body);

		final ObjectNode node = new ObjectMapper().readValue(resData, ObjectNode.class);
		order.setOrderId(node.get("id").asText());
		order.setStatus("OrderPlaced");
		order.setTimeOrderPosteToBsl(NisUtill.getGmtDateFormatISO(new Date()));
		orderRepo.save(order);
		return new ResponseEntity<>(OrderResponseModel.builder().mobileDeviceId(order.getMobileDeviceId())
				.orderId(node.get("id").asText()).build(), HttpStatus.OK);
	}

	@GetMapping(value = "/update-order")
	public ResponseEntity<String> updateOrder(@RequestParam(required = true) String orderId) throws Exception {
		var body = this.getOrderUpdateBody();
		var resData = ncrApiCall.ncrCloudApiCall("https://gateway-staging.ncrcloud.com/order/orders/".concat(orderId),
				HttpMethod.PUT, body);
		return new ResponseEntity<>("done", HttpStatus.OK);
	}

	@GetMapping(value = "/get-last-order")
	public ResponseEntity<Order> getlogo(@RequestHeader Map<String, String> headers,
			@RequestParam(required = true) String mobileDeviceId) throws Exception {
		var o = Order.builder().build();
		return new ResponseEntity<Order>(o, HttpStatus.OK);
	}

	private String getOrderUpdateBody() {
		return "{\r\n" + "	\"channel\": \"Mobile\",\r\n" + "	\"currency\": \"USD\",\r\n" + "	\"customer\": {\r\n"
				+ "		\"email\": \"admin@ncr.com\",\r\n" + "		\"firstName\": \"admin\",\r\n"
				+ "		\"id\": \"R99999999999\",\r\n" + "		\"lastName\": \"admin\",\r\n"
				+ "		\"name\": \"admin admin\",\r\n" + "		\"phone\": \"9999999999\"\r\n" + "	},\r\n" + "	\r\n"
				+ "	\"checkInDetails\": {\r\n" + "		\"application\": \"POS\",\r\n"
				+ "		\"location\": \"Store Front\",\r\n" + "		\"origin\": {\r\n" + "			\"id\": 12,\r\n"
				+ "			\"type\": \"Mobile\"\r\n" + "		},\r\n" + "		\"vector\": {\r\n"
				+ "			\"id\": 75,\r\n" + "			\"type\": \"terminal\"\r\n" + "		}\r\n" + "	},\r\n"
				+ "	\"orderLines\": [\r\n" + "		{\r\n"
				+ "			\"description\": \"The description of the order line\",\r\n"
				+ "			\"extendedAmount\": 20.8,\r\n" + "			\"itemType\": \"Tare\",\r\n"
				+ "			\"linkGroupCode\": \"Retail Group\",\r\n"
				+ "			\"modifierCode\": \"well done\",\r\n" + "			\"overridePrice\": true,\r\n"
				+ "			\"priceModifiers\": [\r\n" + "				{\r\n"
				+ "					\"amount\": 18.99,\r\n" + "					\"description\": \"Extra Cheese\"\r\n"
				+ "				}\r\n" + "			],\r\n" + "			\"productId\": {\r\n"
				+ "				\"type\": \"UPC\",\r\n" + "				\"value\": \"Cheeseburger\"\r\n"
				+ "			},\r\n" + "			\"quantity\": {\r\n" + "				\"unitOfMeasure\": \"EA\",\r\n"
				+ "				\"unitOfMeasureLabel\": \"lbs\",\r\n" + "				\"value\": 78.55\r\n"
				+ "			},\r\n" + "			\"substitutionAllowed\": true,\r\n"
				+ "			\"supplementalData\": \"Any supplemental data that pertains to an order\",\r\n"
				+ "			\"taxes\": [\r\n" + "				{\r\n" + "					\"amount\": 92.85,\r\n"
				+ "					\"code\": \"Sales-Tax\",\r\n" + "					\"isIncluded\": true,\r\n"
				+ "					\"percentage\": 0.07\r\n" + "				}\r\n" + "			],\r\n"
				+ "			\"unitPrice\": 26.5\r\n" + "		}\r\n" + "	],\r\n"
				+ "	\"owner\": \"Ruben's Hamburger Hut\",\r\n" + "	\"partySize\": 1,\r\n" + "	\"payments\": [\r\n"
				+ "		{\r\n" + "			\"accountNumber\": \"12345678901234\",\r\n"
				+ "			\"amount\": 42.1,\r\n" + "			\"description\": \"A description for the payment\",\r\n"
				+ "			\"expiration\": {\r\n" + "				\"month\": 11,\r\n"
				+ "				\"year\": 2031\r\n" + "			},\r\n" + "			\"gratuity\": 48.35,\r\n"
				+ "			\"payBalance\": true,\r\n" + "			\"status\": \"Authorized\",\r\n"
				+ "			\"type\": \"Cash\"\r\n" + "		}\r\n" + "	],\r\n" + "	\"pickupContact\": {\r\n"
				+ "		\"company\": \"NCR\",\r\n" + "		\"hasArrived\": false,\r\n"
				+ "		\"name\": \"Ravinder\",\r\n" + "		\"phone\": \"9999999999\",\r\n"
				+ "		\"vehicle\": {\r\n" + "			\"color\": \"Silver\",\r\n"
				+ "			\"licensePlate\": \"ABC1234\",\r\n" + "			\"make\": \"Porsche\",\r\n"
				+ "			\"model\": \"911 Turbo\",\r\n" + "			\"year\": \"2017\"\r\n" + "		}\r\n"
				+ "	},\r\n" + "	\"promotions\": [\r\n" + "		{\r\n" + "			\"adjustment\": {\r\n"
				+ "				\"applied\": null,\r\n" + "				\"level\": \"ITEM\",\r\n"
				+ "				\"type\": \"PROMO\"\r\n" + "			},\r\n" + "			\"amount\": 50.25,\r\n"
				+ "			\"numGuests\": 97,\r\n" + "			\"orderLineGroups\": [\r\n" + "				{\r\n"
				+ "					\"name\": \"Name of an order line group\",\r\n"
				+ "					\"orderLineIds\": [\r\n" + "						\"order-line-id\"\r\n"
				+ "					]\r\n" + "				}\r\n" + "			],\r\n"
				+ "			\"supportingData\": \"Any extra supporting data of the promotion\"\r\n" + "		}\r\n"
				+ "	],\r\n" + "	\"revenueCenter\": \"bar\",\r\n" + "	\"status\": \"Canceled\",\r\n"
				+ "	\"taxExempt\": false,\r\n" + "	\"taxes\": [\r\n" + "		{\r\n"
				+ "			\"active\": true,\r\n" + "			\"amount\": 6.4,\r\n"
				+ "			\"code\": \"Servoce\",\r\n" + "			\"description\": \"Service tax\",\r\n"
				+ "			\"isIncluded\": false,\r\n" + "			\"percentage\": 52.4\r\n" + "		}\r\n"
				+ "	],\r\n" + "	\"totals\": [\r\n" + "		{\r\n" + "			\"type\": \"Net\",\r\n"
				+ "			\"value\": 27.24\r\n" + "		}\r\n" + "	]\r\n" + "}";
	}

	private String getBody() {
		return "{\r\n" + "	\"channel\": \"Mobile\",\r\n" + "	\"currency\": \"USD\",\r\n" + "	\"customer\": {\r\n"
				+ "		\"email\": \"admin@ncr.com\",\r\n" + "		\"firstName\": \"admin\",\r\n"
				+ "		\"id\": \"admin0098768\",\r\n" + "		\"lastName\": \"admin\",\r\n"
				+ "		\"name\": \"admin admin\",\r\n" + "		\"phone\": \"9999999999\"\r\n" + "	},\r\n" + "	\r\n"
				+ "	\"checkInDetails\": {\r\n" + "		\"application\": \"POS\",\r\n"
				+ "		\"location\": \"Store Front\",\r\n" + "		\"origin\": {\r\n" + "			\"id\": 12,\r\n"
				+ "			\"type\": \"Mobile\"\r\n" + "		},\r\n" + "		\"vector\": {\r\n"
				+ "			\"id\": 75,\r\n" + "			\"type\": \"terminal\"\r\n" + "		}\r\n" + "	},\r\n"
				+ "	\"orderLines\": [\r\n" + "		{\r\n"
				+ "			\"description\": \"The description of the order line\",\r\n"
				+ "			\"extendedAmount\": 20.8,\r\n" + "			\"itemType\": \"Tare\",\r\n"
				+ "			\"linkGroupCode\": \"Retail Group\",\r\n"
				+ "			\"modifierCode\": \"well done\",\r\n" + "			\"overridePrice\": true,\r\n"
				+ "			\"priceModifiers\": [\r\n" + "				{\r\n"
				+ "					\"amount\": 18.99,\r\n" + "					\"description\": \"Extra Cheese\"\r\n"
				+ "				}\r\n" + "			],\r\n" + "			\"productId\": {\r\n"
				+ "				\"type\": \"UPC\",\r\n" + "				\"value\": \"Cheeseburger\"\r\n"
				+ "			},\r\n" + "			\"quantity\": {\r\n" + "				\"unitOfMeasure\": \"EA\",\r\n"
				+ "				\"unitOfMeasureLabel\": \"lbs\",\r\n" + "				\"value\": 78.55\r\n"
				+ "			},\r\n" + "			\"substitutionAllowed\": true,\r\n"
				+ "			\"supplementalData\": \"Any supplemental data that pertains to an order\",\r\n"
				+ "			\"taxes\": [\r\n" + "				{\r\n" + "					\"amount\": 92.85,\r\n"
				+ "					\"code\": \"Sales-Tax\",\r\n" + "					\"isIncluded\": true,\r\n"
				+ "					\"percentage\": 0.07\r\n" + "				}\r\n" + "			],\r\n"
				+ "			\"unitPrice\": 26.5\r\n" + "		}\r\n" + "	],\r\n"
				+ "	\"owner\": \"Ruben's Hamburger Hut\",\r\n" + "	\"partySize\": 1,\r\n" + "	\"payments\": [\r\n"
				+ "		{\r\n" + "			\"accountNumber\": \"12345678901234\",\r\n"
				+ "			\"amount\": 42.1,\r\n" + "			\"description\": \"A description for the payment\",\r\n"
				+ "			\"expiration\": {\r\n" + "				\"month\": 11,\r\n"
				+ "				\"year\": 2031\r\n" + "			},\r\n" + "			\"gratuity\": 48.35,\r\n"
				+ "			\"payBalance\": true,\r\n" + "			\"status\": \"Authorized\",\r\n"
				+ "			\"type\": \"Cash\"\r\n" + "		}\r\n" + "	],\r\n" + "	\"pickupContact\": {\r\n"
				+ "		\"company\": \"NCR\",\r\n" + "		\"hasArrived\": false,\r\n"
				+ "		\"name\": \"Ravinder\",\r\n" + "		\"phone\": \"9999999999\",\r\n"
				+ "		\"vehicle\": {\r\n" + "			\"color\": \"Silver\",\r\n"
				+ "			\"licensePlate\": \"ABC1234\",\r\n" + "			\"make\": \"Porsche\",\r\n"
				+ "			\"model\": \"911 Turbo\",\r\n" + "			\"year\": \"2017\"\r\n" + "		}\r\n"
				+ "	},\r\n" + "	\"promotions\": [\r\n" + "		{\r\n" + "			\"adjustment\": {\r\n"
				+ "				\"applied\": null,\r\n" + "				\"level\": \"ITEM\",\r\n"
				+ "				\"type\": \"PROMO\"\r\n" + "			},\r\n" + "			\"amount\": 50.25,\r\n"
				+ "			\"numGuests\": 97,\r\n" + "			\"orderLineGroups\": [\r\n" + "				{\r\n"
				+ "					\"name\": \"Name of an order line group\",\r\n"
				+ "					\"orderLineIds\": [\r\n" + "						\"order-line-id\"\r\n"
				+ "					]\r\n" + "				}\r\n" + "			],\r\n"
				+ "			\"supportingData\": \"Any extra supporting data of the promotion\"\r\n" + "		}\r\n"
				+ "	],\r\n" + "	\"revenueCenter\": \"bar\",\r\n" + "	\"status\": \"OrderPlaced\",\r\n"
				+ "	\"taxExempt\": false,\r\n" + "	\"taxes\": [\r\n" + "		{\r\n"
				+ "			\"active\": true,\r\n" + "			\"amount\": 6.4,\r\n"
				+ "			\"code\": \"Servoce\",\r\n" + "			\"description\": \"Service tax\",\r\n"
				+ "			\"isIncluded\": false,\r\n" + "			\"percentage\": 52.4\r\n" + "		}\r\n"
				+ "	],\r\n" + "	\"totals\": [\r\n" + "		{\r\n" + "			\"type\": \"Net\",\r\n"
				+ "			\"value\": 27.24\r\n" + "		}\r\n" + "	]\r\n" + "}";
	}

}
