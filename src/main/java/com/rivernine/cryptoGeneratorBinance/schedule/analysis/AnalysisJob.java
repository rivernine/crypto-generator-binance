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

  public String calAskPrice(Symbol symbol, Map<Integer, Order> bidOrders, Integer level, Double usedBalance) {
    return analysisImpl.calAskPrice(symbol, bidOrders, level, usedBalance);
  }

  public Double calLossCutPrice(Map<Integer, Order> bidOrders, Integer level, Double usedBalance) {
    return analysisImpl.calLossCutPrice(bidOrders, level, usedBalance);
  }

  public String getCoinQuantity(Map<Integer, Order> bidOrders, Integer level) {
    return analysisImpl.getCoinQuantity(bidOrders, level);
  }

  public Boolean judgeScaleTrade(Double curPrice, Double lastBidPrice, Integer level) {
    return analysisImpl.judgeScaleTrade(curPrice, lastBidPrice, level);
  }

  public String convertStepSize(Symbol symbol, Double quantity) {
    return analysisImpl.convertStepSize(symbol, quantity);
  }
}
