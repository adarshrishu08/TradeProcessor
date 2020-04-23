package com.example.Trade.service;

import com.example.Trade.model.TradeStore;
import com.example.Trade.repository.TradeRepository;
import com.example.Trade.model.Trade;
import com.example.Trade.repository.TradeStoreRepository;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeProcessorImpl implements TradeProcessor{

    @Autowired
    TradeStoreRepository tradeStoreRepository;

    /**
     * This method Process Trades by getting all existing values from DB if any , and Iterates over a map to match with
     * the received trades.
     * @param trades
     */
    @Override
    public void processTrade(List<Trade> trades) {
        List<TradeStore> tradeStoresToSave = new ArrayList<>();
        if(!trades.isEmpty()){
            TradeUtil<Trade> tradeUtil = new TradeUtil<>();
            //Mapping Trade Id to Trade Object for Trades Received
            Map<String,List<Trade>> tradeIdToTradeMap = tradeUtil.maptradeIdToTrade(trades);
            List<String> tradeIds = trades.stream().distinct().map(t->t.getTradeId()).collect(Collectors.toList());
            List<TradeStore> existingTrades = tradeStoreRepository.findByTradeIdInOrderByTradeIdAsc(tradeIds);
            TradeUtil<TradeStore> tradeStoreUtil = new TradeUtil<>();
            Map<String,List<TradeStore>> tradeStoreIdToTradeMap = tradeStoreUtil.maptradeIdToTrade(existingTrades);
            for(Map.Entry<String, List<Trade>> entry:tradeIdToTradeMap.entrySet()){
                try {
                    validateTrade(tradeStoreIdToTradeMap, entry, tradeStoresToSave);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(!tradeStoresToSave.isEmpty()) {
                saveOrUpdateTradeStore(tradeStoresToSave);
            }
        }
    }

    /**
     * This Method Operates by collecting Information from DB for each and every Trades Receives, We can Limit the Trades for
     * some number of Trades Processed at one time.
     * @param trades
     */
    @Override
    public void processTradeByDB(List<Trade> trades) {
        List<TradeStore> tradeStoresToSave = new ArrayList<>();
        if(!trades.isEmpty()){
            for(Trade trade:trades){
                //Checking if Existing Trade is already available into the Store fro DB, if yes validating the conditons
                List<TradeStore> existingTradesFromDB = tradeStoreRepository.findByTradeId(trade.getTradeId());
                if(!existingTradesFromDB.isEmpty()){
                    existingTradesFromDB.forEach(tradeStore -> {
                        try {
                            validateTradeFromDB(tradeStore, trade, tradeStoresToSave);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    });
                } else {
                    TradeStore newTradeStore = new TradeStore();
                    BeanUtils.copyProperties(trade, newTradeStore);
                    tradeStoresToSave.add(newTradeStore);
                }
            }
            if(!tradeStoresToSave.isEmpty()) {
                saveOrUpdateTradeStore(tradeStoresToSave);
            }
        }
    }

    @Transactional
    public void  saveOrUpdateTradeStore(List<TradeStore> tradeStores){
        synchronized (this) {
            if (!tradeStores.isEmpty()) {
                tradeStoreRepository.saveAll(tradeStores);
            }
        }
    }

    public void validateTradeFromDB(TradeStore tradeStore, Trade trade, List<TradeStore> tradeStoresToSave) throws Exception{
        TradeStore newTradeStore =  null;
        //Throwing Exception if trade version os found lesser than what we already have in store
        if(trade.getVersion().intValue()<tradeStore.getVersion().intValue()){
            throw new Exception("Received Lower version Trade with Version "+trade.getVersion()+" of Trade "+trade.getTradeId()+":: Trade version Mismatch");
        }
        //Updating trade store values with the Received values if version is found equals than the existing stored value
        if(tradeStore.getVersion().intValue() == trade.getVersion().intValue()){
            newTradeStore = new TradeStore();
            BeanUtils.copyProperties(trade,newTradeStore);
            tradeStoresToSave.add(newTradeStore);
        }
        //Discarding those trades to be added in Store if Matuarity date is found less than the current Date.
        if(!trade.getMatuarityDate().before(new Date())){
            if(null==newTradeStore){
                newTradeStore = new TradeStore();
                BeanUtils.copyProperties(trade, newTradeStore);
                tradeStoresToSave.add(newTradeStore);
            }
        }
    }

    public void validateTrade(Map<String,List<TradeStore>> tradeStoreIdToTradeMap, Map.Entry<String, List<Trade>> entry, List<TradeStore> tradeStoresToSave) throws Exception{
        String key = entry.getKey();
        List<Trade> tradesByTradeId = entry.getValue();
        //Checking if Existing Trades from DB has the same Trade Id as of Trade Id Received from transmission
        if(null != tradeStoreIdToTradeMap && tradeStoreIdToTradeMap.containsKey(key)){
            List<TradeStore> tradeStores=tradeStoreIdToTradeMap.get(key);
            tradeStores.sort(Comparator.comparing(TradeStore::getVersion));
            for(Trade trade : tradesByTradeId){
                if(trade.getVersion().intValue()<tradeStores.get(tradeStores.size()-1).getVersion().intValue()){
                    throw new Exception("Received Lower version Trade with Version "+trade.getVersion()+" of Trade "+trade.getTradeId()+":: Trade version Mismatch");
                } else {
                    //Checking if Same Version of Trade is found Matching with the existing Trade in DB, Then Overriding it with the Updated Trade.
                    for(TradeStore store:tradeStores){
                        if(store.getVersion().intValue() == trade.getVersion().intValue()){
                            tradeStoresToSave.add(prepareTradeStore(trade, store));
                        }
                    }
                }
                //Checking if Trade Matuarity Date is Less than Current Date, then Discarding it to Save.
                if(!trade.getMatuarityDate().before(new Date())){
                    TradeStore newTradeStore = new TradeStore();
                    BeanUtils.copyProperties(trade, newTradeStore);
                    tradeStoresToSave.add(newTradeStore);
                }
            }
        } else {
            List<TradeStore> tradeStores=tradesByTradeId.stream().map(t->{
                TradeStore newTradeStore = new TradeStore();
                BeanUtils.copyProperties(t, newTradeStore);
                return newTradeStore;
            }).collect(Collectors.toList());
            tradeStoresToSave.addAll(tradeStores);
        }
    }

    public TradeStore prepareTradeStore(Trade trade, TradeStore tradeStore){
        tradeStore.setBookId(trade.getBookId());
        tradeStore.setCounterPartyId(trade.getCounterPartyId());
        tradeStore.setCreatedDate(trade.getCreatedDate());
        tradeStore.setExpired(trade.getExpired());
        tradeStore.setMatuarityDate(trade.getMatuarityDate());
        return tradeStore;
    }

    @Transactional
    public void updateExpiration(){
        List<TradeStore> expiredTradeStores=tradeStoreRepository.findExpiredTradeStore(new Date());
        if(!expiredTradeStores.isEmpty()) {
            expiredTradeStores.stream().forEach(ts -> {
                ts.setExpired("Y");
            });
            synchronized (this){
                tradeStoreRepository.saveAll(expiredTradeStores);
            }
        }
    }
}
