package com.crypto.service;

import com.crypto.model.ExchangeResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.asynchttpclient.*;
import com.crypto.model.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ExchangeService {

    private Map<String, Map<String, ExchangeResponseData>> exchangeToExchangeRatesMap = new HashMap<>();
    private Map<String, BoundRequestBuilder> exchangeToBoundRequestBuilderMap = new HashMap<>();

    private static final String EXCHANGES_URL = "https://dev-api.shrimpy.io/v1/exchanges/";

    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ExchangeService() {
        StopWatch sw = new StopWatch();
        sw.start();

        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config();
        AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient(clientBuilder);
        BoundRequestBuilder builder = asyncHttpClient.prepareGet(EXCHANGES_URL);
        Future<Response> response = builder.execute();
        Gson gson = new Gson();
        List<String> exchangeList = new ArrayList<>();
        try {
            exchangeList = gson.fromJson(
                    response.get().getResponseBody(),
                    new TypeToken<List<String>>() {
                    }.getType()
            );
            LOGGER.log(Level.INFO, "Got " + exchangeList.size() + " data from API");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            e.getStackTrace();
        }

        for (String exchange : exchangeList) {
            String url = EXCHANGES_URL + exchange + "/ticker";
            LOGGER.log(Level.INFO, url);
            BoundRequestBuilder tempBuilder = asyncHttpClient.prepareGet(url);
            exchangeToBoundRequestBuilderMap.putIfAbsent(exchange.toLowerCase(), tempBuilder);
        }

        sw.stop();
        LOGGER.log(Level.INFO, "ExchangeService Constructor took " + sw.getTotalTimeMillis() + " ms");
    }

    @PostConstruct
    private void init() {
        final long timeInterval = 30000;

        new Thread(() -> {
            while (true) {
                refreshExchangeRates();
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refreshExchangeRates() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (Map.Entry<String, BoundRequestBuilder> exchangeBuilder : exchangeToBoundRequestBuilderMap.entrySet()) {
            Future<Response> response = exchangeBuilder.getValue().execute();
            List<ExchangeResponseData> exchangeResponseDataList = new ArrayList<>();
            Gson gson = new Gson();
            try {
                exchangeResponseDataList = gson.fromJson(
                        response.get().getResponseBody(),
                        new TypeToken<List<ExchangeResponseData>>() {
                        }.getType()
                );
                LOGGER.log(Level.INFO,
                        "Got " + exchangeBuilder.getKey() + " " + exchangeResponseDataList.size() + " " +
                                "data from API");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                e.getStackTrace();
            }
            exchangeToExchangeRatesMap.putIfAbsent(exchangeBuilder.getKey().toLowerCase(), new HashMap<>());
            for (ExchangeResponseData exchangeResponseData : exchangeResponseDataList) {
                exchangeToExchangeRatesMap.get(exchangeBuilder.getKey().toLowerCase())
                        .putIfAbsent(exchangeResponseData.getSymbol().toLowerCase(),
                        exchangeResponseData);
            }
        }
        sw.stop();
        LOGGER.log(Level.INFO, "refreshExchangeRates Took " + sw.getTotalTimeMillis() + " ms");
    }

    public ResponseModel fetchExchangeRate(String exchangeName, String fromCurrency, String toCurrency) {
        StopWatch sw = new StopWatch();
        sw.start();
        ResponseModel responseModel = new ResponseModel();

        if (!exchangeToExchangeRatesMap.containsKey(exchangeName)) {
            responseModel.setErrorResponse("Exchange Name not present");
            return responseModel;
        }

        Long lastModified = null;
        Double fromCurrRate = null, toCurrRate = null;

        Long currentTime = new Date().getTime();
        if(Objects.nonNull(exchangeToExchangeRatesMap.get(exchangeName).get(fromCurrency))) {
            lastModified = exchangeToExchangeRatesMap.get(exchangeName).get(fromCurrency).getLastUpdated().getTime();
            fromCurrRate = exchangeToExchangeRatesMap.get(exchangeName).get(fromCurrency).getPriceUsd();
        } else {
            responseModel.setErrorResponse("From Currency not found.");
        }
        LOGGER.log(Level.INFO, "Current Time: " + new Date(currentTime));
        LOGGER.log(Level.INFO, "Last Modified Time of Crypto Exchange: " + new Date(lastModified));

        if(currentTime - lastModified > 60000) {
            responseModel.setErrorResponse("Exchange Rate is not up-to-date. Please try again after sometime");
            return responseModel;
        }

        if(Objects.nonNull(exchangeToExchangeRatesMap.get(exchangeName).get(toCurrency))) {
            toCurrRate = exchangeToExchangeRatesMap.get(exchangeName).get(toCurrency).getPriceUsd();
        } else {
            responseModel.setErrorResponse("To Currency not found.");
        }

        responseModel.setRate(fromCurrRate/toCurrRate);
        sw.stop();
        LOGGER.log(Level.INFO, "fetchExchangeRate Took " + sw.getTotalTimeMillis() + " ms");

        return responseModel;

    }

}
