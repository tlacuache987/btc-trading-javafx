package com.xvhx.btc.websocket.listener.api.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.xvhx.btc.bitso.domain.DiffOrder;
import com.xvhx.btc.websocket.listener.api.MessageListener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiffOrdersListener implements MessageListener<DiffOrder> {

	private @Getter Queue<DiffOrder> difforders = new ConcurrentLinkedQueue<>();

	@Override
	public void onMessage(DiffOrder diffOrder) {

		difforders.add(diffOrder);
		log.debug("difforder added: {}, queue size: {}", diffOrder.getSequence(), difforders.size());
	}

}
