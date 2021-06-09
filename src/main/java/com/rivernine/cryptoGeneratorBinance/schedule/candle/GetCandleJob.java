package com.rivernine.cryptoGeneratorBinance.schedule.candle;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.impl.GetCandleImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GetCandleJob {
  private final GetCandleImpl getCandleImpl;

  public List<Candle> getCandlesFiveMinutes(String symbol, Integer limit) {
    return getCandleImpl.getCandlesFiveMinutes(symbol, limit);
  }
}
