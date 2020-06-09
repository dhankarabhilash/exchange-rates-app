# exchange-rates-app
=======
# Exchange Rates Finder

## Introduction

Exchange provides exchange rate between two currencies in Double

## Build and Deploy

### Intellij

* Using Intellij just go to ExchangeApplication.java, then go to Run and click on Run 

### Command Line

#### Build Application
```sh
# Change directory to Project Directory
cd /Users/abhilashdhankar/Exchange

# build command
gradle clean build
```

#### Deploy Application

```sh
# Deploy using
java -jar build/libs/Exchange-1.0-SNAPSHOT.jar
```

## Access URL

```sh
API Endpoint: 
http://localhost:8080/getExchangeRate/KUCOIN?fromCurrency=BTC&toCurrency=ETH&format=json

curl request: 
curl --location --request GET 'http://localhost:8080/getExchangeRate/kucoin?fromCurrency=btc&toCurrency=eth'
```
Response:
```json
39.99280129576676
{
    "rate": 39.99280129576676,
    "errorResponse": null
}
```
