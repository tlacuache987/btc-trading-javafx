package com.xvhx.btc.api.rest.domain;

import java.util.List;

import lombok.Data;

@Data
public class BitsoOrderBook {
	private String updated_at;
	private List<BitsoBid> bids;
	private List<BitsoAsk> asks;
	private String sequence;
}
