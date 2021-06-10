package com.rivernine.cryptoGeneratorBinance.schedule.user.Impl;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.AccountBalance;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserImpl {
  @Value("${binance.apiKey}")
  private String apiKey;
  @Value("${binance.secretKey}")
  private String secretKey;

  public void getAccount() {
    SyncRequestClient client = SyncRequestClient.create(apiKey, secretKey);
    log.info(client.getAccountInformation().toString());
  }

  public Balance getBalance() {
    SyncRequestClient client = SyncRequestClient.create(apiKey, secretKey);
    List<AccountBalance> balances = client.getBalance();
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
