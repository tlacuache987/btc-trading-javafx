package com.xvhx.btc.websocket.bitso.domain;

import lombok.Data;

@Data
public class BitsoWsBid {
	private String o;
	private String r;
	private String a;
	private String v;
	private Integer t;
	private Long d;
	private String s;
}
