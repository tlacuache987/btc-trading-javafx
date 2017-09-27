package com.xvhx.btc.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Component
public class BitsoTradingConfig implements InitializingBean {

	@Value("${config.x:20}")
	private String x;

	@Value("${config.m:2}")
	private String m;

	@Value("${config.n:2}")
	private String n;

	private StringProperty xProperty = new SimpleStringProperty();
	private StringProperty mProperty = new SimpleStringProperty();
	private StringProperty nProperty = new SimpleStringProperty();

	public String getX() {
		return xProperty.get();
	}

	public void setX(String x) {
		this.xProperty.set(x);
	}

	public String getM() {
		return mProperty.get();
	}

	public void setM(String m) {
		this.mProperty.set(m);
	}

	public String getN() {
		return nProperty.get();
	}

	public void setN(String n) {
		this.nProperty.set(n);
	}

	public StringProperty xProperty() {
		return xProperty;
	}

	public StringProperty mProperty() {
		return mProperty;
	}

	public StringProperty nProperty() {
		return nProperty;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setM(this.m);
		this.setN(this.n);
		this.setX(this.x);
	}
}
