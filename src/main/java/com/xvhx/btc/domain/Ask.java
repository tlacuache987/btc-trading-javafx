package com.xvhx.btc.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Ask {
	private StringProperty orderId = new SimpleStringProperty();
	private StringProperty amount = new SimpleStringProperty();
	private StringProperty price = new SimpleStringProperty();
	private StringProperty rate = new SimpleStringProperty();

	public Ask() {
	}

	public StringProperty orderIdProperty() {
		return orderId;
	}

	public StringProperty amountProperty() {
		return amount;
	}

	public StringProperty priceProperty() {
		return price;
	}

	public StringProperty rateProperty() {
		return rate;
	}

	public String getOrderId() {
		return orderId.get();
	}

	public void setOrderId(String orderId) {
		this.orderId.set(orderId);
	}

	public String getAmount() {
		return amount.get();
	}

	public void setAmount(String amount) {
		this.amount.set(amount);
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

}
