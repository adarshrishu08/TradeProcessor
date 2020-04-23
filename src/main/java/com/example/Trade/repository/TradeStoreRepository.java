package com.example.Trade.repository;

import com.example.Trade.model.Trade;
import com.example.Trade.model.TradeStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TradeStoreRepository extends JpaRepository<TradeStore, BigDecimal> {

    List<TradeStore> findByTradeIdInOrderByTradeIdAsc(List<String> tradeIds);
    List<TradeStore> findByTradeId(String tradeId);

    @Query(value = "select t from TradeStore t where t.matuarityDate < :currentDate and t.expired<>'Y'")
    List<TradeStore> findExpiredTradeStore(@Param("currentDate")Date currentDate);

}
