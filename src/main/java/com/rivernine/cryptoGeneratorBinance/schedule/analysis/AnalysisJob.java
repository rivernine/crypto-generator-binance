package com.rivernine.cryptoGeneratorBinance.schedule.analysis;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl.AnalysisImpl;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AnalysisJob {
  
  private final AnalysisImpl analysisImpl;

  public Boolean analysisCandles(List<Candle> candles, Integer count) {
    return analysisImpl.analysisCandles(candles, count);
  }
}
