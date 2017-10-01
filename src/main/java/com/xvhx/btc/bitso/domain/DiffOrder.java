package com.xvhx.btc.bitso.domain;

import lombok.Data;

@Data
public class DiffOrder {
	private String type;
	private String book;
	private Long sequence;
	private DiffOrderPayLoad payload;
}
