package com.xvhx.btc.websocket.listener.api.impl;

import static javafx.collections.FXCollections.observableArrayList;

import com.xvhx.btc.bitso.domain.Orders;
import com.xvhx.btc.config.BitsoTradingConfig;
import com.xvhx.btc.websocket.listener.api.MessageListener;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class LineChartOrdersListener implements MessageListener<Orders> {

	// private ObservableList<XYChart.Series<Number, Number>> asks =
	// observableArrayList();
	// private ObservableList<XYChart.Series<Number, Number>> bids =
	// observableArrayList();

	private ObservableList<XYChart.Series<Number, Number>> lines = observableArrayList();

	private SimpleDoubleProperty minProperty = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty maxProperty = new SimpleDoubleProperty(0);

	private ObservableList<Orders> items = observableArrayList();

	private BitsoTradingConfig bitsoTradingConfig;

	public LineChartOrdersListener(BitsoTradingConfig config) {
		this.bitsoTradingConfig = config;
	}

	public ObservableList<Orders> getItems() {
		return items;
	}

	public SimpleDoubleProperty minProperty() {
		return minProperty;
	}

	public SimpleDoubleProperty maxProperty() {
		return maxProperty;
	}

	/*public ObservableList<XYChart.Series<Number, Number>> getAsks() {
		return asks;
	}
	
	public ObservableList<XYChart.Series<Number, Number>> getBids() {
		return bids;
	}*/

	public ObservableList<XYChart.Series<Number, Number>> getLines() {
		return lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Orders message) {

		XYChart.Series<Number, Number> askSerie = new XYChart.Series<>();
		askSerie.setName("Best Asks");

		XYChart.Series<Number, Number> bidSerie = new XYChart.Series<>();
		bidSerie.setName("Best Bids");

		double max = Integer.MIN_VALUE;
		double min = Integer.MAX_VALUE;
		for (int i = 0; i < message.getPayload().getAsks().size(); i++) {

			Double rate = Double.valueOf(message.getPayload().getAsks().get(i).getRate());
			if (rate > max) {
				max = rate;
			}
			if (rate < min) {
				min = rate;
			}
			if (i < Integer.valueOf(bitsoTradingConfig.getX())) {
				askSerie.getData().add(new XYChart.Data<Number, Number>(i, rate));
			} else {
				break;
			}

		}

		for (int i = 0; i < message.getPayload().getBids().size(); i++) {

			Double rate = Double.valueOf(message.getPayload().getBids().get(i).getRate());
			if (rate > max) {
				max = rate;
			}
			if (rate < min) {
				min = rate;
			}
			if (i < Integer.valueOf(bitsoTradingConfig.getX())) {
				bidSerie.getData().add(new XYChart.Data<Number, Number>(i, rate));
			} else {
				break;
			}
		}

		minProperty.set(min - 100);
		maxProperty.set(max + 100);

		Platform.runLater(() -> {
			// asks.setAll(askSerie);
			// bids.setAll(bidSerie);
			lines.setAll(askSerie, bidSerie);
		});
	}

}
