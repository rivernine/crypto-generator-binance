package com.rivernine.cryptoGeneratorBinance.common;

import java.math.BigDecimal;
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
  public Integer step = 0;
  public Symbol symbol;

  public Map<String, Map<LocalDateTime, Candle>> candles;
  public Map<String, Symbol> symbolsInfo;

  public Map<Integer, Order> bidInfoPerLevel;
  public Map<Integer, BigDecimal> bidPricePerLevel;
  public String bidOrderTime;
  public Boolean waitBidOrder;

  public Boolean isStart;
  public BigDecimal usedBalance;

  public Map<Integer, Order> askInfoPerLevel;
  public Boolean waitAskOrder;

  public void increaseLevel() {
    this.level++;
  }

  public void decreaseLevel() {
    this.level--;
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

  public void addBidPricePerLevel(BigDecimal price) {
    this.bidPricePerLevel.put(this.level, price);
  }

  public void addAskInfoPerLevel(Order order) {
    this.askInfoPerLevel.put(this.level, order);
  }

  public void updateUsedBalance(Order order, Integer level) {
    BigDecimal price = order.getPrice();
    BigDecimal quantity = order.getOrigQty();
    BigDecimal usedBalance = price.multiply(quantity).multiply(new BigDecimal(1.0002));
    this.addUsedBalance(usedBalance.multiply(new BigDecimal(level)));
  }

  public void addUsedBalance(BigDecimal balance) {
    this.usedBalance = this.usedBalance.add(balance);
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

    this.isStart = false;
    this.usedBalance = new BigDecimal(0.0);

    this.askInfoPerLevel = new HashMap<>();
    this.waitAskOrder = false;
  }
}
