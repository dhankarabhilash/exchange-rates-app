# Exchange Rates Finder

## Introduction

Exchange provides exchange rate between two currencies in BigDecimal

## Build and Deploy

### Intellij

* Using Intellij just go to ExchangeApplication.java, then go to Run and click on Run 

### Command Line

#### Build Application
```sh
# Change directory to Project Directory
cd D:\Projects\Exchange

# build command
gradlew clean build
```

#### Deploy Application

```sh
# Deploy using
java -jar build/libs/Exchange-1.0-SNAPSHOT.jar
```

## Access URL

```sh
http://localhost:8080/getExchangeRate/KUCOIN?fromCurrency=BTC&toCurrency=ETH&format=json
```
Result will be
```json
39.99280129576676
```

---
