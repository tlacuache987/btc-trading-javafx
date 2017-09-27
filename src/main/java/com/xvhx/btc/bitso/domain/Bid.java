package com.xvhx.btc.bitso.domain;

import lombok.Data;

@Data
public class Bid {
	private String orderId;
	private String rate;
	private String amount;
	private String value;
	private Integer type;
	private Long timestamp;
	private String s;
}
