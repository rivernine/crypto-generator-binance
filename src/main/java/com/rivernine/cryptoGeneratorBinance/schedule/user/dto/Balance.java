package com.rivernine.cryptoGeneratorBinance.schedule.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Balance {

  private String asset;
  private Double balance;
  private Double withdrawAvailable;

  @Builder
  public Balance(String asset, Double balance, Double withdrawAvailable){
    this.asset = asset;
    this.balance = balance;
    this.withdrawAvailable = withdrawAvailable;
  }
}