package com.rivernine.cryptoGeneratorBinance.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

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
  
  @Value("${binance.apiKey}")
  public String apiKey;
  @Value("${binance.secretKey}")
  public String secretKey;
  public SyncRequestClient queryClient;
  public SyncRequestClient invokeClient;

  @Value("${binance.symbols}")
  public List<String> symbols;
  @Value("${bianance.leveragePerLevel}")
  public List<String> leveragePerLevel;

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
    this.queryClient = SyncRequestClient.create();
    this.invokeClient = SyncRequestClient.create(this.apiKey, this.secretKey);
    this.step = 0;
    this.symbol = null;
    this.bidRunning = false;
    this.bidPending = false;
    this.askRunning = false;
    this.askPending = false;

    this.candles = new HashMap<>();  
    for(String symbol: symbols) 
      this.candles.put(symbol, new HashMap<>());
  }
}
