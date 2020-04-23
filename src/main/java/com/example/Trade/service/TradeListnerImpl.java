package com.example.Trade.service;

import com.example.Trade.repository.TradeRepository;
import com.example.Trade.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeListnerImpl implements TradeListner {

    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private TradeProcessor tradeProcessor;

    @Override
    public List<Trade> listenTrade(){
        return tradeRepository.findAll();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *" /*, zone = "Indian/Maldives"*/) //cron = "0 0 0 * * *" is for 12 AM at Night Every Day
    public void markExpiredJob() {
        System.out.println("Scheduled Job triggerd..");
        tradeProcessor.updateExpiration();
    }
}
