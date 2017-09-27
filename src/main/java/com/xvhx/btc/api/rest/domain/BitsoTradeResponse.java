package com.xvhx.btc.api.rest.domain;

import java.util.List;

import lombok.Data;

@Data
public class BitsoTradeResponse {
	private boolean success;
	private List<BitsoTrade> payload;
}
