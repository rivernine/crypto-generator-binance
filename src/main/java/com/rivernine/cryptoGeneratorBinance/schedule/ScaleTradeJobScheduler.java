package com.rivernine.cryptoGeneratorBinance.schedule;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.CandlestickInterval;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScaleTradeJobScheduler {
  @Scheduled(fixedDelay = 1000)
  public void runGetMultipleCandlesJob() {
    log.info("Get Candles Job");
    log.info(SyncRequestClient.create().getCandlestick("BTCUSDT", CandlestickInterval.FIVE_MINUTES, 1499865549590L, 1599865549590L, 1).toString());

  }
}
