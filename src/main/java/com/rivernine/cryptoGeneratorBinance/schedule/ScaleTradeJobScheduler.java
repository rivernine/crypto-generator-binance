package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.CandleJob;
import com.rivernine.cryptoGeneratorBinance.schedule.candle.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.user.UserJob;

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
  private final UserJob userJob;

  @Scheduled(fixedDelay = 1000)
  public void runCollectCandlesJob() {    
    for(String symbol: symbols) {
      candleJob.collectCandlesFiveMinutes(symbol, 4);
    }
  }

  @Scheduled(fixedDelay = 1000)
  public void runScaleTradeJob() {

    List<Candle> candles;
    String symbol = status.getSymbol();

    switch(status.getStep()) {
      case 0:  
        // [ init step ]
        log.info("[0 -> 1] [select market step] ");
        status.init();
        status.setStep(1);
        break;
      case 1:
        // [ select market step ]
        // for(String symb: symbols) {
        //   log.info("< " + symb + " >");
        //   candles = candleJob.getRecentCandles(symb, 3);
        //   if(analysisJob.analysisCandles(candles, 3)) {
        //     log.info("It's time to bid!! My select : " + symb);
        //     log.info("[1 -> 10] [bid step] ");
        //     status.setSymbol(symb);
        //     status.setStep(10);
        //     break;
        //   }
        // }

        // [ select market test step]
        log.info("< BTCUSDT >");
        log.info("It's time to bid!! My select : BTCUSDT");
        log.info("[1 -> 10] [bid step] ");
        status.setSymbol("BTCUSDT");
        status.setStep(10);
        break;
      case 10:
        // [bid step]
        Double myBalance = userJob.getUSDTBalance().getBalance();
        if(myBalance.compareTo(0.0) == 1) {
          log.info("Let's bid~");
        } else {
          log.info("Not enough money.");
        }
        break;
    }
  }
}
