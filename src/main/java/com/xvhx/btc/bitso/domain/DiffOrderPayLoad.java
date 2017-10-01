package com.xvhx.btc.bitso.domain;

import lombok.Data;

@Data
public class DiffOrderPayLoad {
	private Long timestamp;
	private String rate;
	private Integer type;
	private String amount;
	private String value;
	private String status;
	private String orderId;
}
