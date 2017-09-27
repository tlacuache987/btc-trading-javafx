package com.xvhx.btc.websocket.bitso.domain;

import lombok.Data;

@Data
public class BitsoOrdersWsResponse {
	private String type;
	private String book;
	private BitsoOrderWs payload;
}
