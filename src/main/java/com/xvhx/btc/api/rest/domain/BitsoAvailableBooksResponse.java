package com.xvhx.btc.api.rest.domain;

import java.util.List;

import lombok.Data;

@Data
public class BitsoAvailableBooksResponse {
	private boolean success;
	private List<BitsoBook> payload;
}
