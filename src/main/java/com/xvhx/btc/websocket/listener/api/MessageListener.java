package com.xvhx.btc.websocket.listener.api;

@FunctionalInterface
public interface MessageListener<T> {
	void onMessage(T message);
}
