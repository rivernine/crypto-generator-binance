package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Config;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.MarketJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;
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
    Symbol selectedSymbol = status.getSymbol();
    Integer level = status.getLevel();

    switch(status.getStep()) {
      case 0:  
        // [ init step ]
        log.info("[0 -> 1] [select market step] ");
        status.init();
        client.init();
        marketJob.setSymbolsInfo();
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
        status.setSymbol(status.getSymbolsInfo().get("BTCUSDT"));
        status.setStep(10);
        break;
      case 10:
        // [bid step]
        candle = marketJob.getLastCandle(selectedSymbol.getSymbol());
        Double myBalance = userJob.getUSDTBalance().getBalance();
        Double bidBalance = config.getBidBalance() * config.getLeveragePerLevel(level).doubleValue();

        if(myBalance.compareTo(bidBalance) != -1) {
          Double closePrice = candle.getClose();
          Double quantity = bidBalance / closePrice;
          tradeJob.changeInitialLeverage(selectedSymbol.getSymbol(), config.getLeveragePerLevel(level));
          Order bidOrder = tradeJob.bid(selectedSymbol.getSymbol(), quantity.toString(), closePrice.toString());
          if(bidOrder.getStatus().equals("NEW")) {
            log.info("[10 -> 30] [wait step] ");
            status.addBidInfoPerLevel(bidOrder);
            status.addBidPricePerLevel(closePrice);
            status.setBidOrderTime(candle.getTime());
            status.setWaitBidOrder(true);
            status.setStep(30);
          } else {
            log.info("Error during bid.");
          }
        } else {
          log.info("Not enough money.");
        }
        break;
      case 20:
        // [ ask step ]
        orderChanceDtoForAsk = ordersJobConfiguration.getOrdersChanceForAskJob(market);
        log.info("orderChanceDtoForAsk: " + orderChanceDtoForAsk.toString());
        if(Double.parseDouble(orderChanceDtoForAsk.getBalance()) * Double.parseDouble(orderChanceDtoForAsk.getAvgBuyPrice()) > 5000.0){
          askPrice = analysisForScaleTradingJobConfiguration.getAskPriceJob(orderChanceDtoForAsk);
          ordersAskResponseDto = ordersJobConfiguration.askJob(market, orderChanceDtoForAsk.getBalance(), askPrice);
          if(ordersAskResponseDto.getSuccess()) {
            log.info("[changeStatus: 20 -> 30] [wait step] ");
            scaleTradeStatusProperties.addAskInfoPerLevel(ordersAskResponseDto);
            scaleTradeStatusProperties.setWaitingAskOrder(true);
            statusProperties.setCurrentStatus(30);
          } else {
            log.info("Error during asking");
          }
        } else {
          log.info("Not enough coin balance");
        }
        break;
      case 30:
        candle = marketJob.getLastCandle(selectedSymbol.getSymbol());
        String bidOrderTime = status.getBidOrderTime();
        if(status.getWaitBidOrder()) {
          if(!bidOrderTime.equals(candle.getTime())) {
            if(!status.getStart()) 
              log.info("Must find another chance..");
            log.info("[30 -> 40] [cancel bid step] ");
            status.setStep(40);
            break;
          }

          Order bidOrder = status.getBidInfoPerLevel().get(level);
          Order newOrder = tradeJob.getOrder(selectedSymbol.getSymbol(), bidOrder.getOrderId());
          if(newOrder.getStatus().equals("CANCELED")) {
            log.info("Success bidding!!");              
            status.setStart(true);
            status.updateUsedBalance(newOrder);
            status.setWaitBidOrder(false);
            if(status.getWaitAskOrder()) {
              log.info("[30 -> 41] [cancel ask order for bid step] ");
              status.setStep(41);
            } else {
              log.info("[30 -> 20] [ask step] ");
              status.setStep(20);
            }
          } else {
            log.info("Wait for bid");
          }
        }

        break;
    }
    
  }
}
