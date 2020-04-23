package com.example.Trade.service;

import com.example.Trade.model.Trade;

import java.util.List;

public interface TradeListner {
    List<Trade> listenTrade();
    void markExpiredJob();
}
