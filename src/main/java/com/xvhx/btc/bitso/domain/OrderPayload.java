package com.xvhx.btc.bitso.domain;

import java.util.List;

import lombok.Data;

@Data
public class OrderPayload {
	private List<Bid> bids;
	private List<Ask> asks;
}
