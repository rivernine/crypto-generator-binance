package com.rivernine.cryptoGeneratorBinance.schedule.candle.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.CandlestickInterval;
import com.rivernine.cryptoGeneratorBinance.client.model.market.Candlestick;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

import org.springframework.stereotype.Component;

@Component
public class GetCandleImpl {
  public List<Candle> getCandlesFiveMinutes(String symbol, Integer limit) {
    List<Candle> result = new LinkedList<>();
    List<Candlestick> candles = SyncRequestClient.create().getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, null, null, limit);
    for( Candlestick candle: candles ) {
      LocalDateTime ldt = Instant.ofEpochMilli(candle.getOpenTime())
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
      BigDecimal open = candle.getOpen();
      BigDecimal close = candle.getClose();
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
                        .high(candle.getHigh())
                        .low(candle.getLow())
                        .close(close)
                        .flag(flag)
                        .build();
      result.add(element);
    }
    return result;
  }

}
