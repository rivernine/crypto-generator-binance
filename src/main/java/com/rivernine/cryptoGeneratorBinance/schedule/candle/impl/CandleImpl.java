package com.rivernine.cryptoGeneratorBinance.schedule.candle.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.CandlestickInterval;
import com.rivernine.cryptoGeneratorBinance.client.model.market.Candlestick;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CandleImpl {

  private final Status status;

  public List<Candle> collectCandlesFiveMinutes(String symbol, Integer limit) {
    List<Candle> result = new ArrayList<>();
    List<Candlestick> candles = status.getQueryClient().getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, null, null, limit);
    for( Candlestick candle: candles ) {
      LocalDateTime ldt = Instant.ofEpochMilli(candle.getOpenTime())
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
      Double open = candle.getOpen().doubleValue();
      Double close = candle.getClose().doubleValue();
      int flag;
      if(open.compareTo(close) == 1) 
        flag = 1;
      else if(open.compareTo(close) == 0) 
        flag = 0;
      else 
        flag = -1;
      
      Candle element = Candle.builder()
                        .symbol(symbol)
                        .time(ldt.toString())
                        .open(open)
                        .high(candle.getHigh().doubleValue())
                        .low(candle.getLow().doubleValue())
                        .close(close)
                        .flag(flag)
                        .build();
      result.add(element);
      status.addCandles(symbol, LocalDateTime.parse(ldt.toString()), element);
    }
    return result;
  }

  public List<Candle> getRecentCandles(String symbol, Integer count) {
    List<Candle> result = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    Map<LocalDateTime, Candle> candles = status.getCandles().get(symbol);
    List<LocalDateTime> keys = new ArrayList<>(candles.keySet());
    keys.sort((s1, s2) -> s2.format(formatter).compareTo(s1.format(formatter)));
  
    if(keys.size() >= count){
      int addCount = 0;
      for(LocalDateTime key: keys) {
        if(addCount == count)
          break;
        result.add(candles.get(key));
        addCount++;
      }
    } else {
      log.info("Not enough size. Candles size: " + Integer.toString(candles.size()));
    }
    
    return result;
  }

}
