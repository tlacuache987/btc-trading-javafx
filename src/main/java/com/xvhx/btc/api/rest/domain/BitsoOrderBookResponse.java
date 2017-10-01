package com.xvhx.btc.api.rest.domain;

import lombok.Data;

@Data
public class BitsoOrderBookResponse {
	private boolean success;
	private BitsoOrderBook payload;
}
