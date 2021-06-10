package com.rivernine.cryptoGeneratorBinance.schedule.market.dto;

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
  private Double open;      // 시가
  private Double high;      // 고가
  private Double low;       // 저가
  private Double close;     // 종가
  private int flag;             // -1: 음봉, 0: 보합, 1: 양봉

  @Builder
  public Candle(String symbol, String time, Double open, Double high, Double low, Double close, int flag){
    this.symbol = symbol;
    this.time = time;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.flag = flag;
  }
}