package com.example.Trade;

import com.example.Trade.model.Trade;
import com.example.Trade.service.TradeListner;
import com.example.Trade.service.TradeProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class TradeApplication implements CommandLineRunner {

	@Autowired
	TradeListner tradeListner;
	@Autowired
	TradeProcessor tradeProcessor;

	public static void main(String[] args) {
		SpringApplication.run(TradeApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		List<Trade> trades=tradeListner.listenTrade();
		tradeProcessor.processTradeByDB(trades);
	}
}
