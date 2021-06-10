package com.rivernine.cryptoGeneratorBinance.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  public Integer level = 0;
  public Integer step;
  
  public String symbol;
  public Boolean bidRunning, bidPending;
  public Boolean askRunning, askPending;

  public Map<String, Map<LocalDateTime, Candle>> candles;
  public Map<String, Symbol> symbolsInfo;

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

  public void init(){
    this.level = 0;
    this.step = 0;
    this.symbol = null;
    this.bidRunning = false;
    this.bidPending = false;
    this.askRunning = false;
    this.askPending = false;

    this.candles = new HashMap<>();  
    this.symbolsInfo = new HashMap<>();
    for(String symbol: symbols) 
      this.candles.put(symbol, new HashMap<>());
  }
}
