package com.xvhx.btc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class Main extends Application {

	private ApplicationContext applicationContext;
	private Parent root;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void init() throws Exception {
		applicationContext = SpringApplication.run(Main.class,
				this.getParameters().getRaw().toArray(new String[this.getParameters().getRaw().size()]));

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		fxmlLoader.setControllerFactory(applicationContext::getBean);
		root = fxmlLoader.load();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Application.setUserAgentStylesheet(STYLESHEET_MODENA);

		stage.setTitle("BTC Trading Demo - JavaFX");
		Scene scene = new Scene(root);
		scene.getStylesheets().add("css/styles.css");
		stage.setScene(scene);

		stage.show();
	}

	@Override
	public void stop() throws Exception {
		((AbstractApplicationContext) applicationContext).close();
		super.stop();
		System.exit(0);
	}

}
