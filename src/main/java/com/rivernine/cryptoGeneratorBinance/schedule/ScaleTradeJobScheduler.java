package com.rivernine.cryptoGeneratorBinance.schedule;

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
  }
}
