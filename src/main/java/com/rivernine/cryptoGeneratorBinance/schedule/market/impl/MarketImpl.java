package com.rivernine.cryptoGeneratorBinance.schedule.market.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.enums.CandlestickInterval;
import com.rivernine.cryptoGeneratorBinance.client.model.market.Candlestick;
import com.rivernine.cryptoGeneratorBinance.client.model.market.ExchangeInfoEntry;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class MarketImpl {

  private final Status status;
  private final Client client;

  public List<Candle> collectCandlesFiveMinutes(String symbol, Integer limit) {
    List<Candle> result = new ArrayList<>();
    List<Candlestick> candles = client.getQueryClient().getCandlestick(symbol, CandlestickInterval.FIVE_MINUTES, null, null, limit + 1);
    for( int i = 0; i < limit; i++ ) {
      Candlestick candle = candles.get(i);
      LocalDateTime ldt = Instant.ofEpochMilli(candle.getOpenTime())
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
      Double open = candle.getOpen().doubleValue();
      Double close = candle.getClose().doubleValue();
      int flag;
      if(open.compareTo(close) == 1) 
        flag = -1;
      else if(open.compareTo(close) == 0) 
        flag = 0;
      else 
        flag = 1;
      
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

  public void setSymbolsInfo() {
    List<ExchangeInfoEntry> symbols = client.getQueryClient().getExchangeInformation().getSymbols();
    for(ExchangeInfoEntry symbol: symbols) {
      List<List<Map<String, String>>> filters = symbol.getFilters();      
      String tickSize = null;
      String stepSize = null;
      for(List<Map<String, String>> filter: filters) {
        for(Map<String, String> obj: filter) {
          if(obj.containsKey("tickSize")) {
            tickSize = obj.get("tickSize");
          } else if(obj.containsKey("stepSize")) {
            stepSize = obj.get("stepSize");
          }
        }
      }

      status.addSymbolsInfo(symbol.getSymbol(), Symbol.builder()
                                                      .symbolName(symbol.getSymbol())
                                                      .tickSize(tickSize)
                                                      .stepSize(stepSize)
                                                      .build());
    }
  }

}
