package com.rivernine.cryptoGeneratorBinance.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
@NoArgsConstructor
@Getter
@Setter
public class Status {

  // scalping  
  public Integer step = 0;
  // true: Long, false: Short
  public Boolean position;
  public Map<String, Symbol> symbolsInfo = new HashMap<>();
  public Boolean init = false;
  public Order bidOrder = new Order();
  public Order askOrder = new Order();
  public Integer waitCount = 0;
  public BigDecimal avgBuyPrice;

  public void initScalping() {
    this.step = 0;
    this.bidOrder = new Order();
    this.waitCount = 0;
    this.askOrder = new Order();
    this.avgBuyPrice = null;
  }

  // scale trade
  @Value("${binance.symbols}")
  public List<String> symbols;

  public Integer level = 1;
  // public Integer step = 0;
  public Symbol symbol;

  public Map<String, Map<LocalDateTime, Candle>> candles;  

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

  public void updateUsedBalance(Order order) {
    BigDecimal price = order.getPrice();
    BigDecimal quantity = order.getOrigQty();
    BigDecimal usedBalance = price.multiply(quantity).multiply(new BigDecimal(1.0002));    
    // log.info("price : quantity : usedBalance");
    // log.info(price.toString() + " : " + quantity.toString() + " : " + usedBalance);
    this.addUsedBalance(usedBalance);
    // log.info("after updateUsedBalance");
    // log.info(this.usedBalance.toString());
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
