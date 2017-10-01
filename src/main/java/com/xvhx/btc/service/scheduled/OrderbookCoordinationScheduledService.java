package com.xvhx.btc.service.scheduled;

import static javafx.collections.FXCollections.observableArrayList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.xvhx.btc.api.rest.domain.BitsoOrderBookResponse;
import com.xvhx.btc.bitso.domain.DiffOrder;
import com.xvhx.btc.domain.Ask;
import com.xvhx.btc.domain.Bid;
import com.xvhx.btc.websocket.listener.api.impl.DiffOrdersListener;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderbookCoordinationScheduledService extends ScheduledService<Void> {

	private static final String URL_BITSO_ORDERBOOK = "https://api.bitso.com/v3/order_book/";

	private ObservableList<Ask> asks = observableArrayList();
	private ObservableList<Bid> bids = observableArrayList();

	private List<Ask> listBitsoAsk;
	private List<Bid> listBitsoBid;
	private Long lastSequence;

	private LongProperty sequence = new SimpleLongProperty();

	private static boolean firstExecution = true;
	private static long totalExecutions = 0;

	private static boolean recallOrderBookRestEndpoint = false;

	private Long prevSequenceId = null;
	private boolean firstSequenceId = true;

	@Autowired
	private RestTemplate restTemplate;

	@Resource
	private StringProperty markerTradeId;

	@Autowired
	private DiffOrdersListener diffOrdersListener;

	public LongProperty sequence() {
		return this.sequence;
	}

	public ObservableList<Ask> asks() {
		return this.asks;
	}

	public ObservableList<Bid> bids() {
		return this.bids;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				if (diffOrdersListener.getDifforders().size() > 0 || recallOrderBookRestEndpoint) {
					// If has values or if its needed to recall rest api
					if (firstExecution || recallOrderBookRestEndpoint) {
						// Call webservice
						callBitsoOrderBookRestAPI();

						firstExecution = false;
						recallOrderBookRestEndpoint = false;
					} else {

						DiffOrder difforder = diffOrdersListener.getDifforders().peek();

						if (difforder != null) {

							if (difforder.getSequence() <= sequence.get()) { // discard difforder
								log.debug("difforder {} discarded", difforder.getSequence());
								diffOrdersListener.getDifforders().poll();
							} else {

								if (firstSequenceId) {
									firstSequenceId = false;
									prevSequenceId = difforder.getSequence() - 1;
								}
								log.debug("prev sequence: {} compared with: {}", prevSequenceId,
										difforder.getSequence());
								if (prevSequenceId == difforder.getSequence() - 1) {

									if (difforder.getPayload().getType() == 0) { // BUY

										log.trace("sequence {} is a BUY", difforder.getSequence());

										if (difforder.getPayload().getStatus().equalsIgnoreCase("cancelled")) { // remove
																												// element

											log.trace("sequence {} is cancelled", difforder.getSequence());

											@SuppressWarnings("unused")
											boolean found = false;

											Iterator<Bid> bidIterator = listBitsoBid.iterator();

											while (bidIterator.hasNext()) {
												Bid item = bidIterator.next();
												if (item.getOrderId().equals(difforder.getPayload().getOrderId())) {
													bidIterator.remove();
													found = true;
													log.trace("sequence {} is removed", difforder.getSequence());
													break;
												}
											}

											diffOrdersListener.getDifforders().poll();

										} else if (difforder.getPayload().getStatus().equalsIgnoreCase("open")) { // add
																													// element

											log.trace("sequence {} is open", difforder.getSequence());

											Bid bid = new Bid();
											bid.setOrderId(difforder.getPayload().getOrderId());
											bid.setAmount(difforder.getPayload().getAmount());
											bid.setRate(difforder.getPayload().getRate());
											bid.setPrice(String.valueOf(
													Double.valueOf(bid.getAmount()) * Double.valueOf(bid.getRate())));

											listBitsoBid.add(bid);

											log.trace("sequence {} is added", difforder.getSequence());

											Collections.sort(listBitsoBid, new Comparator<Bid>() {
												@Override
												public int compare(Bid bid1, Bid bid2) {
													return Double.compare(Double.valueOf(bid2.getRate()),
															Double.valueOf(bid1.getRate()));
												}
											});

											diffOrdersListener.getDifforders().poll();
										}
									} else if (difforder.getPayload().getType() == 1) { // SELL

										log.trace("sequence {} is a SELL", difforder.getSequence());

										if (difforder.getPayload().getStatus().equalsIgnoreCase("cancelled")) { // remove
																												// element

											log.trace("sequence {} is cancelled", difforder.getSequence());

											@SuppressWarnings("unused")
											boolean found = false;

											Iterator<Ask> askIterator = listBitsoAsk.iterator();

											while (askIterator.hasNext()) {
												Ask item = askIterator.next();
												if (item.getOrderId().equals(difforder.getPayload().getOrderId())) {
													askIterator.remove();
													found = true;
													log.trace("sequence {} is removed", difforder.getSequence());
													break;
												}
											}

											diffOrdersListener.getDifforders().poll();

										} else if (difforder.getPayload().getStatus().equalsIgnoreCase("open")) { // add
																													// element

											log.trace("sequence {} is open", difforder.getSequence());

											Ask ask = new Ask();
											ask.setOrderId(difforder.getPayload().getOrderId());
											ask.setAmount(difforder.getPayload().getAmount());
											ask.setRate(difforder.getPayload().getRate());
											ask.setPrice(String.valueOf(
													Double.valueOf(ask.getAmount()) * Double.valueOf(ask.getRate())));

											listBitsoAsk.add(ask);

											log.trace("sequence {} is added", difforder.getSequence());

											Collections.sort(listBitsoAsk, new Comparator<Ask>() {
												@Override
												public int compare(Ask ask1, Ask ask2) {
													return Double.compare(Double.valueOf(ask2.getRate()),
															Double.valueOf(ask1.getRate()));
												}
											});

											diffOrdersListener.getDifforders().poll();
										}
									}
								} else {
									// llamar ws de nuevo
									recallOrderBookRestEndpoint = true;
									log.debug("sequence {} is in no-sync, recall orderbook REST API",
											difforder.getSequence());
									firstExecution = true;
								}
							}

							if (!recallOrderBookRestEndpoint) {

								prevSequenceId = difforder.getSequence();

								log.trace("set prev sequence Id");

								listBitsoAsk = listBitsoAsk.stream().limit(20).collect(
										Collectors.toCollection(() -> Collections.synchronizedList(new ArrayList<>())));

								log.trace("set bitso ask list");

								listBitsoBid = listBitsoBid.stream().limit(20).collect(
										Collectors.toCollection(() -> Collections.synchronizedList(new ArrayList<>())));

								log.trace("set bitso bid list");

								lastSequence = prevSequenceId;
							}
						}
					}

					if (!recallOrderBookRestEndpoint) {

						Platform.runLater(() -> {

							asks.setAll(listBitsoAsk);

							log.trace("binding asks list");

							bids.setAll(listBitsoBid);

							log.trace("binding bids list");

							sequence.set(lastSequence);

							log.trace("bind last sequence");

						});
					}

				}

				return null;
			}

			private void callBitsoOrderBookRestAPI() {
				HttpHeaders headers = new HttpHeaders();
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL_BITSO_ORDERBOOK).queryParam("book",
						"btc_mxn").queryParam("aggregate", false);

				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.add("user-agent", "REST-TEMPLATE");

				HttpEntity<?> entity = new HttpEntity<>(headers);

				HttpEntity<BitsoOrderBookResponse> response = null;

				URI uri = builder.build().encode().toUri();

				log.debug("{}) requesting {} ", (++totalExecutions), uri);

				try {
					response = restTemplate.exchange(uri, HttpMethod.GET, entity, BitsoOrderBookResponse.class);
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}

				listBitsoAsk = response.getBody().getPayload().getAsks().stream().limit(20).map(bitsoAsk -> {
					Ask ask = new Ask();
					ask.setOrderId(bitsoAsk.getOid());
					ask.setAmount(bitsoAsk.getAmount());
					ask.setRate(bitsoAsk.getPrice());
					ask.setPrice(String.valueOf(Double.valueOf(ask.getAmount()) * Double.valueOf(ask.getRate())));
					return ask;
				}).collect(Collectors.toCollection(() -> Collections.synchronizedList(new ArrayList<>())));

				log.debug("creating bitso ask list");

				listBitsoBid = response.getBody().getPayload().getBids().stream().limit(20).map(bitsoAsk -> {
					Bid bid = new Bid();
					bid.setOrderId(bitsoAsk.getOid());
					bid.setAmount(bitsoAsk.getAmount());
					bid.setRate(bitsoAsk.getPrice());
					bid.setPrice(String.valueOf(Double.valueOf(bid.getAmount()) * Double.valueOf(bid.getRate())));
					return bid;
				}).collect(Collectors.toCollection(() -> Collections.synchronizedList(new ArrayList<>())));

				log.debug("creating bitso bid list");

				lastSequence = Long.valueOf(response.getBody().getPayload().getSequence());

				log.trace("setting last sequence as: {}, given by REST API", lastSequence);
			}
		};

	}
}
