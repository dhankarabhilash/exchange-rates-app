package com.crypto;

import com.crypto.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@RestController
public class Controller {

    @Autowired
    private ExchangeService exchangeService;

    @RequestMapping(value = "/getExchangeRate/{exchangeName}", method = RequestMethod.GET)
    public BigDecimal getExchangeRate(
            @PathVariable("exchangeName") String exchangeName,
            @RequestParam(value = "fromCurrency") String fromCurrency,
            @RequestParam(value = "toCurrency") String toCurrency) throws ExecutionException, InterruptedException {

        return exchangeService.fetchExchangeRate(exchangeName, fromCurrency, toCurrency);
    }
}
