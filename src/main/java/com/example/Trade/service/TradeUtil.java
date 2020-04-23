package com.example.Trade.service;

import com.example.Trade.model.Trade;
import com.example.Trade.model.TradeStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeUtil<E> {
    E e;
    /*public TradeUtil(E e){
        this.e=e;
    }*/

    public Map<String, List<E>> maptradeIdToTrade(List<E> trades){
        Map<String, List<E>> tradeIdToTradeListMap = null;
        for(E e:trades){
            if(tradeIdToTradeListMap == null){
                tradeIdToTradeListMap  = new HashMap<>();
            }
            if(e instanceof Trade){
                Trade trade = (Trade)e;
                if(tradeIdToTradeListMap.containsKey(trade.getTradeId())){
                    List<E> tradeList = tradeIdToTradeListMap.get(trade.getTradeId());
                    tradeList.add(e);
                    tradeIdToTradeListMap.put(trade.getTradeId(),tradeList);
                } else {
                    List<E> tradeList=new ArrayList<>();
                    tradeList.add(e);
                    tradeIdToTradeListMap.put(trade.getTradeId(),tradeList);
                }
            }
            if(e instanceof TradeStore){
                TradeStore trade = (TradeStore)e;
                if(tradeIdToTradeListMap.containsKey(trade.getTradeId())){
                    List<E> tradeList = tradeIdToTradeListMap.get(trade.getTradeId());
                    tradeList.add(e);
                    tradeIdToTradeListMap.put(trade.getTradeId(),tradeList);
                } else {
                    List<E> tradeList=new ArrayList<>();
                    tradeList.add(e);
                    tradeIdToTradeListMap.put(trade.getTradeId(),tradeList);
                }
            }
        }
        return tradeIdToTradeListMap;
    }
}
