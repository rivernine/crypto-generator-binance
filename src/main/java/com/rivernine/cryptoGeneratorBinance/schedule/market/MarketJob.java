package com.rivernine.cryptoGeneratorBinance.schedule.market;

import java.math.BigDecimal;
import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.model.market.OrderBook;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.impl.MarketImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MarketJob {
  private final MarketImpl marketImpl;

  // scalping
  public OrderBook getOrderBook(String symbol) {
    return marketImpl.getOrderBook(symbol);
  }

  public Candle getCandleOneMinute(String symbol) {
    return marketImpl.getCandleOneMinute(symbol);
  }

  // scale trade
  public List<Candle> collectCandlesFiveMinutes(String symbol, Integer limit) {
    return marketImpl.collectCandlesFiveMinutes(symbol, limit);
  }

  public List<Candle> collectCandlesThreeMinutes(String symbol, Integer limit) {
    return marketImpl.collectCandlesThreeMinutes(symbol, limit);
  }

  public BigDecimal getMarketPrice(String symbol) {
    return marketImpl.getMarketPrice(symbol);
  } 

  public List<Candle> getRecentCandles(String symbol, Integer count) {
    return marketImpl.getRecentCandles(symbol, count);
  }

  public Candle getLastCandle(String symbol) {
    return marketImpl.getRecentCandles(symbol, 1).get(0);
  }

  public void setSymbolsInfo() {
    marketImpl.setSymbolsInfo();
  }
}
