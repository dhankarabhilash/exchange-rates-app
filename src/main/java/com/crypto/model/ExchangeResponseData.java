package com.crypto.model;

import java.util.Date;

public class ExchangeResponseData {

    private String name;
    private String symbol;
    private Double priceUsd;
    private Double priceBtc;
    private Double percentChange24hUsd;
    private Date lastUpdated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Double getPriceBtc() {
        return priceBtc;
    }

    public void setPriceBtc(Double priceBtc) {
        this.priceBtc = priceBtc;
    }

    public Double getPercentChange24hUsd() {
        return percentChange24hUsd;
    }

    public void setPercentChange24hUsd(Double percentChange24hUsd) {
        this.percentChange24hUsd = percentChange24hUsd;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
