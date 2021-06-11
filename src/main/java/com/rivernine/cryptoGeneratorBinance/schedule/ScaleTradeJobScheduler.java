package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Config;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.MarketJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.trade.TradeJob;
import com.rivernine.cryptoGeneratorBinance.schedule.user.UserJob;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScaleTradeJobScheduler {

  private final Status status;
  private final Client client;
  private final Config config;
  
  private final MarketJob marketJob;
  private final AnalysisJob analysisJob;  
  private final UserJob userJob;
  private final TradeJob tradeJob;

  @Scheduled(fixedDelay = 1000000)
  public void runTestJob() {
    status.init();
    client.init();
    marketJob.setSymbolsInfo();

    log.info(status.getSymbolsInfo().toString());
  }

  // @Scheduled(fixedDelay = 1000)
  public void runCollectCandlesJob() {    
    List<String> symbols = status.getSymbols();
    for(String symbol: symbols) {
      marketJob.collectCandlesFiveMinutes(symbol, 4);
    }
  }

  // @Scheduled(fixedDelay = 1000)
  public void runScaleTradeJob() {
    List<Candle> candles;
    Candle candle;
    String symbol = status.getSymbol();
    Integer level = status.getLevel();

    switch(status.getStep()) {
      case 0:  
        // [ init step ]
        log.info("[0 -> 1] [select market step] ");
        status.init();
        client.init();
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
        candle = marketJob.getLastCandle(symbol);
        Double myBalance = userJob.getUSDTBalance().getBalance();
        Double bidBalance = config.getBidBalance() * config.getLeveragePerLevel(level).doubleValue();

        if(myBalance.compareTo(bidBalance) != -1) {
          log.info("Let's bid~");
          Double close = candle.getClose();
          Double volume = bidBalance / close;

        } else {
          log.info("Not enough money.");
        }
        break;
    }
  }
}
