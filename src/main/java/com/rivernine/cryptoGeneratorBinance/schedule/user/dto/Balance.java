package com.rivernine.cryptoGeneratorBinance.schedule.user.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Balance {

  private String asset;
  private BigDecimal balance;
  private BigDecimal withdrawAvailable;

  @Builder
  public Balance(String asset, BigDecimal balance, BigDecimal withdrawAvailable){
    this.asset = asset;
    this.balance = balance;
    this.withdrawAvailable = withdrawAvailable;
  }
}