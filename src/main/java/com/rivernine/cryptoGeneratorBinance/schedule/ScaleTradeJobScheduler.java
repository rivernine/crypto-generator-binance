package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.CandleJob;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScaleTradeJobScheduler {

  @Value("${binance.symbols}")
  private List<String> symbols;

  private final Status status;
  private final CandleJob candleJob;
  private final AnalysisJob analysisJob;  

  @Scheduled(fixedDelay = 1000)
  public void runCollectCandlesJob() {    
    for(String symbol: symbols) {
      candleJob.collectCandlesFiveMinutes(symbol, 4);
    }
  }

  @Scheduled(fixedDelay = 1000)
  public void runScaleTradeJob() {

    List<Candle> candles;
    // String symbol = status.getSymbol();

    switch(status.getStep()) {
      case -1:  
        // [ init step ]
        log.info("[-1 -> 1] [select market step] ");
        status.init();
        status.setStep(1);
        break;
      case 0:
        // [ crytpto-generator start step ]
        log.info("[crypto-generator-binance start step] ");
        status.init();
        status.setStep(1);
        break;
      case 1:
        // [ select market step ]
        for(String symb: symbols) {
          log.info("< " + symb + " >");
          candles = candleJob.getRecentCandles(symb, 3);
          if(analysisJob.analysisCandles(candles, 3)) {
            log.info("It's time to bid!! My select : " + symb);
            log.info("[1 -> 10] [bid step] ");
            status.setSymbol(symb);
            status.setStep(10);
            break;
          }
        }
        break;
      case 10:
        log.info("This is bid step");
        break;
    }
  }
}
