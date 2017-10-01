package com.xvhx.btc.api.rest.domain;

import lombok.Data;

@Data
public class BitsoAsk {
	private String book;
	private String price;
	private String amount;
	private String oid;
}
