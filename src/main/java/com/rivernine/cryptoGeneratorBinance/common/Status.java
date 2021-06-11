package com.rivernine.cryptoGeneratorBinance.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Component
@NoArgsConstructor
@Getter
@Setter
public class Status {
  
  @Value("${binance.symbols}")
  public List<String> symbols;

  public Integer level = 1;
  public Integer step;
  public Symbol symbol;

  public Map<String, Map<LocalDateTime, Candle>> candles;
  public Map<String, Symbol> symbolsInfo;

  public Map<Integer, Order> bidInfoPerLevel;
  public Map<Integer, Double> bidPricePerLevel;
  public String bidOrderTime;
  public Boolean waitBidOrder;

  public Boolean start;
  public Double usedBalance;

  public Map<Integer, Order> askInfoPerLevel;
  public Boolean waitAskOrder;

  public void increaseLevel() {
    this.level++;
  }

  public void addCandles(String symbol, LocalDateTime key, Candle candleDto) {
    if(!this.candles.get(symbol).containsKey(key)) {
      Map<LocalDateTime, Candle> candle = this.candles.get(symbol);
      candle.put(key, candleDto);
      this.candles.put(symbol, candle);
    }
  }

  public void addSymbolsInfo(String symbol, Symbol info) {
    this.symbolsInfo.put(symbol, info);
  }

  public void addBidInfoPerLevel(Order order) {
    this.bidInfoPerLevel.put(this.level, order);
  }

  public void addBidPricePerLevel(Double price) {
    this.bidPricePerLevel.put(this.level, price);
  }

  public void addAskInfoPerLevel(Order order) {
    this.askInfoPerLevel.put(this.level, order);
  }

  public void updateUsedBalance(Order order) {
    Double price = order.getPrice().doubleValue();
    Double quantity = order.getOrigQty().doubleValue();
    this.addUsedBalance((price * quantity) * (1 + 0.0002));
  }

  public void addUsedBalance(Double balance) {
    this.usedBalance += balance;
  }

  public void init(){
    this.level = 1;
    this.step = 0;
    this.symbol = null;

    this.candles = new HashMap<>();  
    this.symbolsInfo = new HashMap<>();
    for(String symbol: symbols) 
      this.candles.put(symbol, new HashMap<>());

    this.bidInfoPerLevel = new HashMap<>();
    this.bidPricePerLevel = new HashMap<>();
    this.bidOrderTime = null;
    this.waitBidOrder = false;

    this.start = false;
    this.usedBalance = 0.0;

    this.askInfoPerLevel = new HashMap<>();
    this.waitAskOrder = false;
  }
}
