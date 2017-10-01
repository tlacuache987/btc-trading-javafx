package com.xvhx.btc.websocket.bitso.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;
import com.xvhx.btc.bitso.domain.Ask;
import com.xvhx.btc.bitso.domain.Bid;
import com.xvhx.btc.bitso.domain.DiffOrder;
import com.xvhx.btc.bitso.domain.DiffOrderPayLoad;
import com.xvhx.btc.bitso.domain.OrderPayload;
import com.xvhx.btc.bitso.domain.Orders;
import com.xvhx.btc.websocket.listener.api.MessageListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ClientEndpoint
public class WebsocketBitsoClient {

	private final List<MessageListener<Orders>> ordersListeners = new ArrayList<>();
	private final List<MessageListener<DiffOrder>> diffOrdersListeners = new ArrayList<>();

	private final URI serverEndpoint;
	private Gson gson;
	private Session session;
	private boolean tryToReconnect;

	private WebSocketContainer container;

	public WebsocketBitsoClient(String serverEndpoint, Gson gson) {
		this.serverEndpoint = URI.create(serverEndpoint);
		this.gson = gson;
	}

	@OnMessage
	public void onWebSocketText(String text) throws IOException {

		@SuppressWarnings("unchecked")
		Map<String, Object> map = gson.fromJson(text, Map.class);

		if (!map.containsKey("action") && map.containsKey("type")
				&& map.get("type").equals("orders")) {

			if (ordersListeners.size() > 0) {
				ordersListeners.stream().forEach(messageListener -> messageListener.onMessage(parseOrder(map)));
				log.trace("message has sent to orders listeners");
			}
		} else if (!map.containsKey("action") && map.containsKey("type")
				&& map.get("type").equals("diff-orders")) {

			if (diffOrdersListeners.size() > 0) {
				diffOrdersListeners.stream().forEach(messageListener -> messageListener.onMessage(parseDiffOrder(map)));
				log.trace("message has sent to difforders listeners");
			}
		}

	}

	private DiffOrder parseDiffOrder(Map<String, Object> map) {
		DiffOrder diffOrder = new DiffOrder();
		diffOrder.setType(map.get("type").toString());
		diffOrder.setBook(map.get("book").toString());
		diffOrder.setSequence(((Double) map.get("sequence")).longValue());
		diffOrder.setPayload(new DiffOrderPayLoad());

		@SuppressWarnings("unchecked")
		Map<String, Object> payload = ((List<Map<String, Object>>) map.get("payload")).get(0);

		diffOrder.getPayload().setOrderId(payload.get("o").toString());
		diffOrder.getPayload().setTimestamp(((Double) payload.get("d")).longValue());
		diffOrder.getPayload().setRate(payload.get("r").toString());
		diffOrder.getPayload().setType(((Double) payload.get("t")).intValue());
		diffOrder.getPayload().setStatus(payload.get("s").toString());

		if (diffOrder.getPayload().getStatus().equals("open")) {
			diffOrder.getPayload().setAmount(payload.get("a").toString());
			diffOrder.getPayload().setValue(payload.get("v").toString());
		}

		return diffOrder;
	}

	private Orders parseOrder(Map<String, Object> map) {
		Orders orders = new Orders();
		orders.setType((String) map.get("type"));
		orders.setBook((String) map.get("book"));
		orders.setPayload(new OrderPayload());

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> bidsMapList = (List<Map<String, Object>>) ((Map<String, Object>) map.get("payload"))
				.get("bids");

		List<Bid> bidsList = bidsMapList.stream().map(m -> {
			Bid bid = new Bid();
			bid.setOrderId(String.valueOf(m.get("o")));
			bid.setAmount(String.valueOf(m.get("a")));
			bid.setRate(String.valueOf(m.get("r")));
			bid.setType(Double.valueOf(String.valueOf(m.get("t"))).intValue());
			bid.setS(String.valueOf(m.get("s")));
			bid.setTimestamp(Double.valueOf(String.valueOf(m.get("t"))).longValue());
			bid.setValue(String.valueOf(m.get("v")));
			return bid;
		}).collect(Collectors.toList());

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> asksMapList = (List<Map<String, Object>>) ((Map<String, Object>) map.get("payload"))
				.get("asks");

		List<Ask> asksList = asksMapList.stream().map(m -> {
			Ask ask = new Ask();
			ask.setOrderId(String.valueOf(m.get("o")));
			ask.setAmount(String.valueOf(m.get("a")));
			ask.setRate(String.valueOf(m.get("r")));
			ask.setType(Double.valueOf(String.valueOf(m.get("t"))).intValue());
			ask.setS(String.valueOf(m.get("s")));
			ask.setTimestamp(Double.valueOf(String.valueOf(m.get("t"))).longValue());
			ask.setValue(String.valueOf(m.get("v")));
			return ask;
		}).collect(Collectors.toList());

		orders.getPayload().setBids(bidsList);
		orders.getPayload().setAsks(asksList);

		return orders;
	}

	@OnError
	public void onError(Throwable error) {
		close();
		retryReconnect();
	}

	@OnClose
	public void onClose() {
		if (tryToReconnect)
			retryReconnect();
	}

	public void addOrdersListener(MessageListener<Orders> listener) {
		ordersListeners.add(listener);
	}

	public void addDiffOrdersListener(MessageListener<DiffOrder> listener) {
		diffOrdersListeners.add(listener);
	}

	public void connect() {
		container = ContainerProvider.getWebSocketContainer();
		try {
			session = container.connectToServer(this, serverEndpoint);

			session.getBasicRemote()
					.sendText("{ \"action\": \"subscribe\", \"book\": \"btc_mxn\", \"type\": \"orders\" }");
			session.getBasicRemote()
					.sendText("{ \"action\": \"subscribe\", \"book\": \"btc_mxn\", \"type\": \"diff-orders\" }");
		} catch (DeploymentException | IOException e) {
			log.error(e.getMessage());
		}
	}

	public void close() {
		if (session != null) {
			try {
				session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE,
						"sesison close"));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	public void close(boolean tryToReconnect) {
		this.tryToReconnect = tryToReconnect;
		close();
	}

	public void closeWithoutReconnect() {
		this.tryToReconnect = false;
		close();
	}

	private void retryReconnect() {
		try {
			TimeUnit.SECONDS.sleep(2);
			connect();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}
}
