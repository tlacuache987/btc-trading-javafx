package com.xvhx.btc._config;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.gson.Gson;
import com.xvhx.btc.config.BitsoTradingConfig;
import com.xvhx.btc.websocket.bitso.client.WebsocketBitsoClient;
import com.xvhx.btc.websocket.listener.api.impl.DiffOrdersListener;
import com.xvhx.btc.websocket.listener.api.impl.LineChartOrdersListener;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Configuration
public class ApplicationConfig {

	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public StringProperty markerTradeId() {
		return new SimpleStringProperty();
	}

	@Bean(initMethod = "connect", destroyMethod = "closeWithoutReconnect")
	public WebsocketBitsoClient wsclient(LineChartOrdersListener lineChartMessageListener,
			DiffOrdersListener diffOrdersListener, Gson gson) {

		WebsocketBitsoClient wsclient = new WebsocketBitsoClient("wss://ws.bitso.com", gson);

		wsclient.addOrdersListener(lineChartMessageListener);
		wsclient.addDiffOrdersListener(diffOrdersListener);

		return wsclient;
	}

	@Bean
	public LineChartOrdersListener lineChartOrdersListener(BitsoTradingConfig config) {
		return new LineChartOrdersListener(config);
	}

	@Bean
	public DiffOrdersListener diffOrdersListener() {
		return new DiffOrdersListener();
	}

	@Bean
	public DecimalFormat mxnCurrencyDecimalFormatter() {
		return new DecimalFormat("$ ###,###,##0.00 MXN");
	}

	@Bean
	public DecimalFormat btcCurrencyDecimalFormatter() {
		return new DecimalFormat("###,##0.00000000 BTC");
	}

	@Bean
	public ISO8601DateFormat iso8601TimestampFormatter() {
		return new ISO8601DateFormat();
	}

	@Bean
	public SimpleDateFormat timestampFormatter() {
		return new SimpleDateFormat("MMM d, y h:mm:ss a");
	}
}
