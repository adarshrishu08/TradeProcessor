package com.example.Trade.repository;

import com.example.Trade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, BigDecimal> {

}
