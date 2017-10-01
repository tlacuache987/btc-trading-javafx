# BITSO Trading Demo using JavaFX

To execute this demo its needed:
  - Java 8
  - Maven 3.2.x+

# How to build

1.- Open a Terminal and clone this repo then build maven project.

```sh
$ git clone https://github.com/tlacuache987/btc-trading-javafx.git
$ cd btc-trading-javafx/
$ mvn clean package
```

# How to execute

Option 1: Simple execute using maven.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ mvn spring-boot:run
```

wait and JavaFX application will open automatically

By default x, m and n values are set as:

x=20, m=2, n=2.

---------------------------------------------------------------------------


Option 2: Execute using maven, passing x, m and n parameters.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ mvn spring-boot:run -Drun.arguments="--config.x=15,--config.m=4,--config.n=3"
```

You can set the parameters x, m and n passing the -Drun.arguments paramter with a value of the --config.x, --config.m and --config.n parameters to the java execution.

wait and JavaFX application will open automatically

---------------------------------------------------------------------------


Option 3: Simple execute using java.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ java -jar target/btc-trading-javafx-0.0.1-SNAPSHOT.jar
```

wait and JavaFX application will open automatically

By default x, m and n values are set as:

x=20, m=2, n=2.

---------------------------------------------------------------------------


Option 4: Execute using java, passing x, m and n parameters.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ java -jar target/btc-trading-javafx-0.0.1-SNAPSHOT.jar --config.x=15 --config.m=4 --config.n=3
```

You can set the parameters x, m and n passing the --config.x, --config.m and --config.n parameters to the java execution.

wait and JavaFX application will open automatically


# Running the app

In the first Tab you can see the 'X' best asks/bids in a line chart, It displays the Available Books on the Bitso API and the X, M and N configuration values as well.

![Best X bids/asks](https://cdn.pbrd.co/images/GMZ0f2W.png)

---------------------------------------------------------------------------


Going further, in the second tab, you can see the latests Trades retrieved by the REST API provided by Bitso.

The application executes the algorithm to create new Fake Buy/Sell Trades from the beginning, that is configured by the 'M' and 'N' variables for to increase the uptick and downtick counts.

![Trades](https://cdn.pbrd.co/images/GMZ2jRd.png)

---------------------------------------------------------------------------


At the third tab it includes the best 20 bids/asks from the order book btc_mxn retrieved by first time using Bitso API and then coordinated through websockets.

Each time the websocket receives a message and its Id doesn't match with the next message id expected, the application refresh the order book through Bitso API and then, again it's coordinated through websockets.

![Bids/Asks coordination](https://cdn.pbrd.co/images/GMZ3Vvh.png)


Have fun !

---------------------------------------------------------------------------