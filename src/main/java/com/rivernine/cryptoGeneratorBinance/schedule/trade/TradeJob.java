package com.rivernine.cryptoGeneratorBinance.schedule.trade;

import com.rivernine.cryptoGeneratorBinance.schedule.trade.impl.TradeImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TradeJob {
  private final TradeImpl tradeImpl;

  public void sample() {
    tradeImpl.sample();
  }

}
