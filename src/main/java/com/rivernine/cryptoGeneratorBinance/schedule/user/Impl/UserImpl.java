package com.rivernine.cryptoGeneratorBinance.schedule.user.impl;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.AccountBalance;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserImpl {

  private final Status status;
  private final Client client;

  public Balance getUSDTBalance() {
    List<AccountBalance> balances = client.getInvokeClient().getBalance();
    Balance result = new Balance();
    for(AccountBalance balance: balances) {
      if(balance.getAsset().equals("USDT")) {
        result = Balance.builder() 
                        .asset(balance.getAsset())
                        .balance(balance.getBalance().doubleValue())
                        .withdrawAvailable(balance.getWithdrawAvailable().doubleValue())
                        .build();
      }
    }
    return result;
  }

}
