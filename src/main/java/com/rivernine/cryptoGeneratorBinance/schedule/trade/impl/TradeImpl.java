package com.rivernine.cryptoGeneratorBinance.schedule.trade.impl;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.market.ExchangeInfoEntry;
import com.rivernine.cryptoGeneratorBinance.client.model.market.ExchangeInformation;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Status;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TradeImpl {

  private final Status status;
  private final Client client;

  public void sample() {
    
  }

}
