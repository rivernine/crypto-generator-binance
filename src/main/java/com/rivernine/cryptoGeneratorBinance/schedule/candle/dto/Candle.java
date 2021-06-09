package com.rivernine.cryptoGeneratorBinance.schedule.candle.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Candle {
  private String symbol;
  private String time;
  private BigDecimal open;      // 시가
  private BigDecimal high;      // 고가
  private BigDecimal low;       // 저가
  private BigDecimal close;     // 종가
  private int flag;             // -1: 음봉, 0: 보합, 1: 양봉

  @Builder
  public Candle(String symbol, String time, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, int flag){
    this.symbol = symbol;
    this.time = time;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.flag = flag;
  }
}