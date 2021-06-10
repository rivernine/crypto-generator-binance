package com.rivernine.cryptoGeneratorBinance.schedule.user.Impl;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.AccountBalance;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserImpl {

  private final Status status;

  public Balance getUSDTBalance() {
    List<AccountBalance> balances = status.getInvokeClient().getBalance();
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
