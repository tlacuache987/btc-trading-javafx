package com.xvhx.btc.websocket.bitso.domain;

import java.util.List;

import lombok.Data;

@Data
public class BitsoOrderWs {
	private List<BitsoWsBid> bids;
	private List<BitsoWsAsk> asks;
}
