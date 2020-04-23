package com.example.Trade.service;

import com.example.Trade.model.Trade;

import java.util.List;

public interface TradeProcessor {
    void processTrade(List<Trade> trades);
    void processTradeByDB(List<Trade> trades);
    void updateExpiration();
}
