package com.rivernine.cryptoGeneratorBinance.common;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Component
@NoArgsConstructor
@Getter
@Setter
public class Config {
                    
  // @Value("${binance.leverages}")
  // public List<Integer> leverages;
  // @Value("${binance.bidBalance}")
  // public BigDecimal bidBalance;
  @Value("${binance.bidBalancePerLevel}")
  public List<BigDecimal> bidBalancePerLevel;

  public BigDecimal getBidBalance(Integer level) {
    return this.bidBalancePerLevel.get(level - 1);
  }
  // public Integer getLeveragePerLevel(Integer level) {
  //   return this.leverages.get(level - 1);
  // }
}
