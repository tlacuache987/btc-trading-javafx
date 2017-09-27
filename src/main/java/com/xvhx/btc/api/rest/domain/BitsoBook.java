package com.xvhx.btc.api.rest.domain;

import lombok.Data;

@Data
public class BitsoBook {
	private String book;
	private String minimum_price;
	private String maximum_price;
	private String minimum_amount;
	private String maximum_amount;
	private String minimum_value;
	private String maximum_value;
}
