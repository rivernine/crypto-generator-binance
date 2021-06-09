package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.schedule.candle.GetCandleJob;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScaleTradeJobScheduler {

  private final GetCandleJob getCandleJob;

  @Scheduled(fixedDelay = 1000)
  public void runGetMultipleCandlesJob() {    
    List<Candle> candles = getCandleJob.getCandlesFiveMinutes("BTCUSDT", 2);
    for(Candle candle: candles) {
      log.info(candle.toString());
    }
  }
}
