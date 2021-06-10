package com.rivernine.cryptoGeneratorBinance.schedule.account.Impl;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccountImpl {
  @Value("${binance.apiKey}")
  private String apiKey;
  @Value("${binance.secretKey}")
  private String secretKey;

  public void getAccount() {
    SyncRequestClient client = SyncRequestClient.create(apiKey, secretKey);
    log.info(client.getAccountInformation().toString());
  }

}
