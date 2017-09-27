package com.xvhx.btc.domain;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Trade {
	private LongProperty tradeId = new SimpleLongProperty();
	private StringProperty tradeType = new SimpleStringProperty();
	private StringProperty tickType = new SimpleStringProperty();
	private StringProperty book = new SimpleStringProperty();
	private StringProperty createdAt = new SimpleStringProperty();
	private StringProperty amount = new SimpleStringProperty();
	private StringProperty markerSide = new SimpleStringProperty();
	private StringProperty price = new SimpleStringProperty();
	private StringProperty rate = new SimpleStringProperty();

	public Trade() {
	}

	public LongProperty tradeIdProperty() {
		return tradeId;
	}

	public StringProperty tradeTypeProperty() {
		return tradeType;
	}

	public StringProperty tickTypeProperty() {
		return tickType;
	}

	public StringProperty bookProperty() {
		return book;
	}

	public StringProperty createdAtProperty() {
		return createdAt;
	}

	public StringProperty amountProperty() {
		return amount;
	}

	public StringProperty markerSideProperty() {
		return markerSide;
	}

	public StringProperty priceProperty() {
		return price;
	}

	public StringProperty rateProperty() {
		return rate;
	}

	public Long getTradeId() {
		return tradeId.get();
	}

	public void setTradeId(Long tradeId) {
		this.tradeId.set(tradeId);
	}

	public String getBook() {
		return book.get();
	}

	public void setBook(String book) {
		this.book.set(book);
	}

	public String getCreatedAt() {
		return createdAt.get();
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt.set(createdAt);
	}

	public String getAmount() {
		return amount.get();
	}

	public void setAmount(String amount) {
		this.amount.set(amount);
	}

	public String getMarkerSide() {
		return markerSide.get();
	}

	public void setMarkerSide(String markerSide) {
		this.markerSide.set(markerSide);
	}

	public String getPrice() {
		return price.get();
	}

	public void setPrice(String price) {
		this.price.set(price);
	}

	public String getRate() {
		return rate.get();
	}

	public void setRate(String rate) {
		this.rate.set(rate);
	}

	public String getTradeType() {
		return tradeType.get();
	}

	public void setTradeType(String tradeType) {
		this.tradeType.set(tradeType);
	}

	public String getTickType() {
		return tickType.get();
	}

	public void setTickType(String tickType) {
		this.tickType.set(tickType);
	}
}
