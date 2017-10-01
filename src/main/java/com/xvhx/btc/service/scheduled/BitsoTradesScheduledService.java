package com.xvhx.btc.service.scheduled;

import static javafx.collections.FXCollections.observableArrayList;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.xvhx.btc.api.rest.domain.BitsoTradeResponse;
import com.xvhx.btc.config.BitsoTradingConfig;
import com.xvhx.btc.domain.Trade;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BitsoTradesScheduledService extends ScheduledService<BitsoTradeResponse> {

	private static final String URL_BITSO_TRADES = "https://api.bitso.com/v3/trades/";

	private ObservableList<Trade> trades = observableArrayList();
	private StringProperty currentPrice = new SimpleStringProperty();
	private IntegerProperty upticksCounter = new SimpleIntegerProperty();
	private IntegerProperty downtickCounter = new SimpleIntegerProperty();

	private static boolean firstExecution = true;
	private static boolean analizeNextTrades = false;
	private static long nextFakeTradeId = 1;
	private static long totalExecutions = 0;

	@Autowired
	private RestTemplate restTemplate;

	@Resource
	private StringProperty markerTradeId;

	@Autowired
	private BitsoTradingConfig bitsoTradingConfig;

	@Autowired
	private ISO8601DateFormat iso8601TimestampFormatter;

	public StringProperty currentPrice() {
		return this.currentPrice;
	}

	public IntegerProperty upticksCounter() {
		return this.upticksCounter;
	}

	public IntegerProperty downtickCounter() {
		return this.downtickCounter;
	}

	public ObservableList<Trade> getTrades() {
		return this.trades;
	}

	@Override
	protected Task<BitsoTradeResponse> createTask() {

		return new Task<BitsoTradeResponse>() {

			@Override
			protected BitsoTradeResponse call() throws Exception {
				HttpHeaders headers = new HttpHeaders();
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL_BITSO_TRADES).queryParam("book",
						"btc_mxn");

				if (markerTradeId != null && markerTradeId.get() != null) {
					builder.queryParam("marker", markerTradeId.get()).queryParam("sort", "asc");
				}

				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.add("user-agent", "REST-TEMPLATE");

				HttpEntity<?> entity = new HttpEntity<>(headers);

				HttpEntity<BitsoTradeResponse> response = null;

				URI uri = builder.build().encode().toUri();

				log.debug("{}) requesting {} ", (++totalExecutions), uri);

				try {
					response = restTemplate.exchange(uri, HttpMethod.GET, entity, BitsoTradeResponse.class);
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}

				final String lastMarkerId = String.valueOf(response.getBody().getPayload().stream()
						.mapToInt(bitsoTrade -> bitsoTrade.getTid().intValue()).max().getAsInt());

				List<Trade> elements = response.getBody().getPayload().stream().map(bitsoTrade -> {
					Trade t = new Trade();
					t.setTradeId(bitsoTrade.getTid());
					t.setAmount(bitsoTrade.getAmount());
					t.setBook(bitsoTrade.getBook());
					t.setMarkerSide(bitsoTrade.getMaker_side().toUpperCase());
					t.setTradeType("Real Trade");
					t.setCreatedAt(bitsoTrade.getCreated_at());
					t.setRate(bitsoTrade.getPrice());
					t.setPrice(String
							.valueOf(Double.valueOf(bitsoTrade.getAmount()) * Double.valueOf(bitsoTrade.getPrice())));
					return t;
				}).collect(Collectors.toList());

				if (!firstExecution) {
					Collections.reverse(elements);
					elements.addAll(trades);
				}

				final List<Trade> listTrades = elements.stream().limit(Long.valueOf(bitsoTradingConfig.getX()))
						.collect(Collectors.toList());

				Platform.runLater(() -> {
					try {
						markerTradeId.set(lastMarkerId);
						currentPrice.set(listTrades.get(0).getRate());

						setTicks(listTrades);
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}

					if (firstExecution) {
						trades.addAll(listTrades);
						firstExecution = false;
					} else {
						trades.setAll(listTrades);
					}

				});

				return response.getBody();
			}

			private void setTicks(List<Trade> elements) {

				@SuppressWarnings("unused")
				int iterations = 0;
				Trade prevTrade = null;

				int elementsSize = elements.size();

				if (elementsSize > 1) {
					for (int i = elementsSize - 1; i >= 0; i--) {
						if (elements.get(i).getTickType() == null) {
							for (int j = i + 1; j <= elementsSize - 1; j++) {
								iterations++;
								prevTrade = elements.get(j);

								if (prevTrade.getTickType() != null && prevTrade.getTickType().equals("zero-tick")) {
									continue;
								}

								if (prevTrade.getRate().equals(elements.get(i).getRate())) {
									elements.get(i).setTickType("zero-tick");

									log.trace("zero-tick !");

								} else if (Double.valueOf(prevTrade.getRate()) > Double
										.valueOf(elements.get(i).getRate())) {

									elements.get(i).setTickType("downtick");

									if (analizeNextTrades) {
										downtickCounter.set(downtickCounter.get() + 1);

										log.trace("downtick up !");

										upticksCounter.set(0);

										if (verifyBuyFakeTrade(elements, i)) {
											// elementsSize++;
										}
										i++;
										j--;
									}
								} else if (Double.valueOf(prevTrade.getRate()) < Double
										.valueOf(elements.get(i).getRate())) {

									elements.get(i).setTickType("uptick");

									if (analizeNextTrades) {
										upticksCounter.set(upticksCounter.get() + 1);

										log.trace("uptick up !");

										downtickCounter.set(0);

										if (verifySellFakeTrade(elements, i)) {
											// elementsSize++;
										}
										i++;
										j--;
									}
								}
								break;
							}
						}
					}
					analizeNextTrades = true;
				}
			}

			private boolean verifySellFakeTrade(List<Trade> elements, int index) {

				if (upticksCounter.get() >= Integer.valueOf(bitsoTradingConfig.getM())) {
					Trade fakeTrade = createFakeTrade("SELL");

					elements.add(index, fakeTrade);
					log.trace("adding SELL fake trade");

					upticksCounter.set(0);
					return true;
				}
				return false;
			}

			private boolean verifyBuyFakeTrade(List<Trade> elements, int index) {

				if (downtickCounter.get() >= Integer.valueOf(bitsoTradingConfig.getN())) {
					Trade fakeTrade = createFakeTrade("BUY");

					elements.add(index, fakeTrade);
					log.trace("adding BUY fake trade");

					downtickCounter.set(0);
					return true;
				}
				return false;
			}

			private Trade createFakeTrade(String type) {
				Trade trade = new Trade();
				trade.setTradeId(nextFakeTradeId++);
				trade.setAmount("1.0");
				trade.setBook("btc_mxn");
				trade.setMarkerSide(type.toUpperCase());
				trade.setTradeType("Fake Trade");
				trade.setCreatedAt(iso8601TimestampFormatter.format(new Date()));
				trade.setRate(currentPrice.get());
				trade.setPrice(currentPrice.get());
				return trade;
			}
		};

	}

}
