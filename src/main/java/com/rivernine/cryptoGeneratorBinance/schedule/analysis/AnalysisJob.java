package com.rivernine.cryptoGeneratorBinance.schedule.analysis;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
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

  public String calAskPrice(Integer level, Symbol symbol, String coinQuantity, Double usedBalance) {
    return analysisImpl.calAskPrice(level, symbol, coinQuantity, usedBalance);
  }

  public Double calLossCutPrice(String coinQuantity, Double usedBalance) {
    return analysisImpl.calLossCutPrice(coinQuantity, usedBalance);
  }

  public Boolean judgeScaleTrade(Double curPrice, Double lastBidPrice, Integer level) {
    return analysisImpl.judgeScaleTrade(curPrice, lastBidPrice, level);
  }

  public String convertStepSize(Symbol symbol, Double quantity) {
    return analysisImpl.convertStepSize(symbol, quantity);
  }
}
