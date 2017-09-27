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
Yo can set the parameters x, m and n passing the -Drun.arguments paramter with a value of the --config.x, --config.m and --config.n parameters to the java execution.

wait and JavaFX application will open automatically

---------------------------------------------------------------------------


Option 3: Simple execute using java.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ java -jar target/btc-trading-javafx-0.0.1-SNAPSHOT.jar
```

open http://localhost:8088/index.html in browser

wait and JavaFX application will open automatically

x=20, m=2, n=2.

---------------------------------------------------------------------------


Option 4: Execute using java, passing x, m and n parameters.

```sh
$ pwd
/<your-path>/btc-trading-javafx/
$ java -jar target/btc-trading-javafx-0.0.1-SNAPSHOT.jar --config.x=15 --config.m=4 --config.n=3
```
Yo can set the parameters x, m and n passing the --config.x, --config.m and --config.n parameters to the java execution.

wait and JavaFX application will open automatically


# Running the app

Section under construction...


Have fun !

---------------------------------------------------------------------------