package com.rivernine.cryptoGeneratorBinance.schedule.analysis;

import java.math.BigDecimal;
import java.util.List;

import com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl.AnalysisImpl;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AnalysisJob {
  
  private final AnalysisImpl analysisImpl;

  public Boolean analysisCandles(List<Candle> candles, Integer count) {
    return analysisImpl.analysisCandles(candles, count);
  }

  public String calAskPrice(Integer level, Symbol symbol, String coinQuantity, BigDecimal usedBalance) {
    return analysisImpl.calAskPrice(level, symbol, coinQuantity, usedBalance);
  }

  public BigDecimal calLossCutPrice(String coinQuantity, BigDecimal usedBalance) {
    return analysisImpl.calLossCutPrice(coinQuantity, usedBalance);
  }

  public Boolean judgeScaleTrade(BigDecimal curPrice, BigDecimal lastBidPrice, Integer level) {
    return analysisImpl.judgeScaleTrade(curPrice, lastBidPrice, level);
  }

  public String convertStepSize(Symbol symbol, BigDecimal quantity) {
    return analysisImpl.convertStepSize(symbol, quantity);
  }
}
