package com.rivernine.cryptoGeneratorBinance.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Config;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.MarketJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.trade.TradeJob;
import com.rivernine.cryptoGeneratorBinance.schedule.user.UserJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScalpJobScheduler {

  @Value("${binance.symbol}")
  public String symbol;

  private final Status status;
  private final Client client;
  private final Config config;
  
  private final MarketJob marketJob;
  private final AnalysisJob analysisJob;  
  private final UserJob userJob;
  private final TradeJob tradeJob;

  @Scheduled(fixedDelay = 500)
  public void runInit() {  
    if(client.getQueryClient() == null || client.getInvokeClient() == null) {
      client.init();
    }  
    Candle candle = marketJob.getCandleOneMinute(symbol);
    if(!status.getTime().equals(candle.getTime())) {
      status.setTime(candle.getTime());
      status.setMp(new HashMap<BigDecimal, Integer>());
    }
  }

  @Scheduled(fixedDelay = 100)
  public void runScalping() {    
    BigDecimal price = marketJob.getMarketPrice(symbol);
    price = price.setScale(0, RoundingMode.HALF_UP);
    Map<BigDecimal, Integer> mp = status.getMp();
    if(mp.containsKey(price)) {
      mp.put(price, mp.get(price) + 1);
    } else {
      mp.put(price, 1);
    }


    // OrderBook orderBook = marketJob.getOrderBook(symbol);
  }

  @Scheduled(fixedDelay = 10000)
  public void printMp() {
    List<Entry<BigDecimal, Integer>> list = new ArrayList<>(status.getMp().entrySet());
		list.sort(Entry.comparingByValue());
    System.out.println("-------------");
		list.forEach(System.out::println);
  }
}
