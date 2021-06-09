package com.rivernine.cryptoGeneratorBinance.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Component
@NoArgsConstructor
@Getter
@Setter
public class Status {
  @Value("${binance.symbols}")
  public List<String> symbols;
  
  public int step;
  public String symbol;
  public Boolean bidRunning, bidPending;
  public Boolean askRunning, askPending;

  public Map<String, Map<LocalDateTime, Candle>> candles;

  public void addCandles(String symbol, LocalDateTime key, Candle candleDto) {
    if(!this.candles.get(symbol).containsKey(key)) {
      Map<LocalDateTime, Candle> candle = this.candles.get(symbol);
      candle.put(key, candleDto);
      this.candles.put(symbol, candle);
    }
  }

  public void init(){
    this.step = 0;
    this.symbol = null;
    this.bidRunning = false;
    this.bidPending = false;
    this.askRunning = false;
    this.askPending = false;

    this.candles = new HashMap<>();  
    for(String symbol: symbols) {
      this.candles.put(symbol, new HashMap<>());
    }
  }
}
