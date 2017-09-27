package com.xvhx.btc.bitso.domain;

import lombok.Data;

@Data
public class Orders {
	private String type;
	private String book;
	private OrderPayload payload;
}
