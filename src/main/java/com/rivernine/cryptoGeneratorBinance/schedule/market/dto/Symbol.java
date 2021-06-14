package com.rivernine.cryptoGeneratorBinance.schedule.market.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Symbol {
  private String symbolName;
  private String tickSize;  // price
  private String stepSize;  // quantity

  @Builder
  public Symbol(String symbolName, String tickSize, String stepSize){
    this.symbolName = symbolName;
    this.tickSize = tickSize;
    this.stepSize = stepSize;
  }
}