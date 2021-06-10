package com.rivernine.cryptoGeneratorBinance.schedule.market.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Symbol {
  private String symbol;
  private String tickSize;
  private String stepSize;

  @Builder
  public Symbol(String symbol, String tickSize, String stepSize){
    this.symbol = symbol;
    this.tickSize = tickSize;
    this.stepSize = stepSize;
  }
}