package com.xvhx.btc.controllers;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.xvhx.btc.config.BitsoTradingConfig;
import com.xvhx.btc.domain.Trade;
import com.xvhx.btc.service.scheduled.BitsoAvailableBooksScheduledService;
import com.xvhx.btc.service.scheduled.BitsoTradesScheduledService;
import com.xvhx.btc.websocket.listener.api.impl.LineChartOrdersListener;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

@Component
public class ControllerFXML implements Initializable {

	@FXML
	private BorderPane principalBorderPane;

	@FXML
	private TabPane tabPane;

	@FXML
	private LineChart<Number, Number> bidsAsksChart;

	@FXML
	private Label availableBooksLabel;

	@FXML
	private NumberAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	@FXML
	private Label latestTradeIdLabel;

	@FXML
	private Label xmnParametersLabel1;

	@FXML
	private Label xmnParametersLabel2;

	@FXML
	private Label currentPriceLabel;

	@FXML
	private Label uptickDowntickLabel;

	@FXML
	private TableView<Trade> tradesTableView;

	@FXML
	private TableColumn<Trade, String> tradeIdColumn;

	@FXML
	private TableColumn<Trade, String> tradeTypeColumn;

	@FXML
	private TableColumn<Trade, String> markerSideColumn;

	@FXML
	private TableColumn<Trade, String> tickTypeColumn;

	@FXML
	private TableColumn<Trade, String> timestampColumn;

	@FXML
	private TableColumn<Trade, String> amountColumn;

	@FXML
	private TableColumn<Trade, String> atColumn;

	@FXML
	private TableColumn<Trade, String> rateColumn;

	@FXML
	private TableColumn<Trade, String> equalsColumn;

	@FXML
	private TableColumn<Trade, String> priceColumn;

	@Autowired
	private DecimalFormat mxnCurrencyDecimalFormatter;

	@Autowired
	private ISO8601DateFormat iso8601TimestampFormatter;

	@Autowired
	private SimpleDateFormat timestampFormatter;

	@Autowired
	private BitsoTradingConfig bitsoTradingconfig;

	@Resource
	private StringProperty markerTradeId;

	@Autowired
	private LineChartOrdersListener lineChartMessageListener;

	@Autowired
	private BitsoAvailableBooksScheduledService bitsoAvailableBooksScheduledService;

	@Autowired
	private BitsoTradesScheduledService bitsoTradesScheduledService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setUpBestBidsAsksTab();

		setUpTradesBook();
	}

	private void setUpTradesBook() {

		xmnParametersLabel2.textProperty().bind(xmnParametersLabel1.textProperty());

		bitsoTradesScheduledService.setPeriod(Duration.seconds(3));
		bitsoTradesScheduledService.setDelay(Duration.millis(100));
		bitsoTradesScheduledService.start();

		latestTradeIdLabel.textProperty()
				.bind(Bindings.concat("(latest Trade Id: ", markerTradeId, ")"));

		uptickDowntickLabel.textProperty()
				.bind(Bindings.concat("(upticks: ", bitsoTradesScheduledService.upticksCounter(),
						", downtick: ", bitsoTradesScheduledService.downtickCounter(), ")"));

		currentPriceLabel.textProperty().bind(
				Bindings.createStringBinding(() -> {
					if (bitsoTradesScheduledService.currentPrice().get() == null)
						return "";
					return "1 BTC = " + mxnCurrencyDecimalFormatter
							.format(Double.parseDouble(bitsoTradesScheduledService.currentPrice().get()));

				}, bitsoTradesScheduledService.currentPrice()));

		tradeIdColumn.setCellValueFactory(new PropertyValueFactory<>("tradeId"));
		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
		timestampColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
		rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		tradeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("tradeType"));
		tickTypeColumn.setCellValueFactory(new PropertyValueFactory<>("tickType"));
		markerSideColumn.setCellValueFactory(new PropertyValueFactory<>("markerSide"));

		atColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText("@");
				setAlignment(Pos.CENTER);
			}
		});
		atColumn.setPrefWidth(30);
		atColumn.setResizable(false);

		equalsColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText("=");
				setAlignment(Pos.CENTER);
			}
		});
		equalsColumn.setPrefWidth(30);
		equalsColumn.setResizable(false);

		priceColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(mxnCurrencyDecimalFormatter.format(Double.valueOf(item)));
				}
			}
		});
		priceColumn.setPrefWidth(130);
		priceColumn.setResizable(false);

		rateColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(mxnCurrencyDecimalFormatter.format(Double.valueOf(item)));
				}
			}
		});
		rateColumn.setPrefWidth(130);
		rateColumn.setResizable(false);

		amountColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.concat(" BTC"));
				}
			}
		});
		amountColumn.setPrefWidth(110);
		amountColumn.setResizable(false);

		timestampColumn.setCellFactory(column -> new TableCell<Trade, String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					String timestampString = null;
					try {
						timestampString = timestampFormatter.format(iso8601TimestampFormatter.parse(item));
					} catch (ParseException e) {
					}
					if (timestampString != null)
						setText(Character.toUpperCase(timestampString.charAt(0)) + timestampString.substring(1));
					else
						setText("");
				}
			}
		});
		timestampColumn.setPrefWidth(160);
		timestampColumn.setResizable(false);

		tradesTableView.setRowFactory(tv -> new TableRow<Trade>() {
			@Override
			public void updateItem(Trade item, boolean empty) {
				super.updateItem(item, empty);
				setAlignment(Pos.BASELINE_CENTER);
				if (item == null) {
					setStyle("");
				} else if (item.getTradeType().equalsIgnoreCase("REAL TRADE")) {
					if (item.getMarkerSide().equals("SELL")) {
						setStyle("-fx-background-color: #D9EDF7;");
					} else if (item.getMarkerSide().equals("BUY")) {
						setStyle("-fx-background-color: #DFF0D8;");
					}
				} else if (item.getTradeType().equalsIgnoreCase("FAKE TRADE")) {
					setStyle("-fx-background-color: #F2DEDE;");
				}
			}
		});

		tradesTableView.setItems(bitsoTradesScheduledService.getTrades());

		tradesTableView.setFixedCellSize(25);

		if (Integer.valueOf(bitsoTradingconfig.getX()) <= 15) {
			tradesTableView.prefHeightProperty().bind(
					tradesTableView.fixedCellSizeProperty()
							.multiply(Bindings.size(tradesTableView.getItems()).add(1.001d)));
			tradesTableView.minHeightProperty().bind(tradesTableView.prefHeightProperty());
			tradesTableView.maxHeightProperty().bind(tradesTableView.prefHeightProperty());
		}
	}

	private void setUpBestBidsAsksTab() {

		xmnParametersLabel1.textProperty().bind(
				Bindings.concat("(X: ", bitsoTradingconfig.xProperty(), ", M: ", bitsoTradingconfig.mProperty(),
						", N: ", bitsoTradingconfig.nProperty(), ")"));

		bitsoAvailableBooksScheduledService.setPeriod(Duration.seconds(3));
		bitsoAvailableBooksScheduledService.setDelay(Duration.millis(100));
		bitsoAvailableBooksScheduledService.start();

		availableBooksLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			if (bitsoAvailableBooksScheduledService.lastValueProperty().get() == null)
				return "";
			return bitsoAvailableBooksScheduledService.lastValueProperty().get().getPayload().stream()
					.map(book -> {
						return (book.getBook().equalsIgnoreCase("btc_mxn"))
								? (book.getBook().toUpperCase() + " (min. price: " +
										mxnCurrencyDecimalFormatter.format(Double.parseDouble(book.getMinimum_price()))
										+ ", max price: " +
										mxnCurrencyDecimalFormatter.format(Double.parseDouble(book.getMaximum_price()))
										+ ")")
								: book.getBook().toUpperCase();
					})
					.collect(Collectors.joining(", "));

		}, bitsoAvailableBooksScheduledService.lastValueProperty()));

		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(Double.valueOf(bitsoTradingconfig.getX()) - 1);
		xAxis.setTickUnit(1);

		yAxis.setAutoRanging(false);
		yAxis.lowerBoundProperty().bind(lineChartMessageListener.minProperty());
		yAxis.upperBoundProperty().bind(lineChartMessageListener.maxProperty());
		yAxis.setTickUnit(100);

		bidsAsksChart.setAnimated(false);

		bidsAsksChart.dataProperty().setValue(lineChartMessageListener.getLines());
	}

}
