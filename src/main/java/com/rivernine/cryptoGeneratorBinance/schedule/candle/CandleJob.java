package com.rivernine.cryptoGeneratorBinance.schedule.candle;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.impl.CandleImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CandleJob {
  private final CandleImpl candleImpl;

  public List<Candle> collectCandlesFiveMinutes(String symbol, Integer limit) {
    return candleImpl.collectCandlesFiveMinutes(symbol, limit);
  }

  public List<Candle> getRecentCandles(String symbol, Integer count) {
    return candleImpl.getRecentCandles(symbol, count);
  }

  public Candle getLastCandle(String symbol) {
    return candleImpl.getRecentCandles(symbol, 1).get(0);
  }
}
