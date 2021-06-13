package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderState;
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
        for(String symb: status.getSymbols()) {
          log.info("< " + symb + " >");
          candles = marketJob.getRecentCandles(symb, 3);
          if(analysisJob.analysisCandles(candles, 3)) {
            log.info("It's time to bid!! My select : " + symb);
            log.info("[1 -> 10] [bid step] ");
            Symbol select = status.getSymbolsInfo().get(symb);
            status.setSymbol(select);
            status.setStep(10);
            break;
          }
        }

        // [ select market test step]
        // log.info("< BTCUSDT >");
        // log.info("It's time to bid!! My select : BTCUSDT");
        // log.info("[1 -> 10] [bid step] ");
        // status.setSymbol(status.getSymbolsInfo().get("BTCUSDT"));
        // status.setStep(10);
        break;
      case 10:
        // [bid step]
        candle = marketJob.getLastCandle(selectedSymbol.getSymbol());
        Double myBalance = userJob.getUSDTBalance().getBalance();
        Double bidBalance = config.getBidBalance() * config.getLeveragePerLevel(level).doubleValue();

        if(myBalance.compareTo(bidBalance) != -1) {
          Double closePrice = candle.getClose();
          String quantity = analysisJob.convertStepSize(selectedSymbol, bidBalance / closePrice);
          tradeJob.changeInitialLeverage(selectedSymbol.getSymbol(), config.getLeveragePerLevel(level));
          Order bidOrder = tradeJob.bid(selectedSymbol.getSymbol(), quantity, closePrice.toString());
          if(bidOrder.getStatus().equals("NEW")) {
            log.info("[10 -> 30] [wait step]");
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
        Map<Integer, Order> bidOrders = status.getBidInfoPerLevel();
        String askPrice = analysisJob.calAskPrice(selectedSymbol, bidOrders, level, status.getUsedBalance());
        String askQuantity = analysisJob.getCoinQuantity(bidOrders, level);
        Order askOrder = tradeJob.ask(selectedSymbol.getSymbol(), askQuantity, askPrice);
        if(askOrder.getStatus().equals("NEW")) {
          log.info("[20 -> 30] [wait step]");
          status.addAskInfoPerLevel(askOrder);
          status.setWaitAskOrder(true);
          status.setStep(30);
        } else {
          log.info("Error during ask.");
        }
        break;
      case 30:
        candle = marketJob.getLastCandle(selectedSymbol.getSymbol());
        String lastbidOrderTime = status.getBidOrderTime();
        if(status.getWaitBidOrder()) {
          if(!lastbidOrderTime.equals(candle.getTime())) {
            if(!status.getIsStart()) 
              log.info("Must find another chance..");
            log.info("[30 -> 40] [cancel bid step] ");
            status.setStep(40);
            break;
          }

          Order oldBidOrder = status.getBidInfoPerLevel().get(level);
          Order newBidOrder = tradeJob.getOrder(selectedSymbol.getSymbol(), oldBidOrder.getOrderId());
          if(newBidOrder.getStatus().equals(OrderState.FILLED)) {
            log.info("Success bidding!!");              
            status.setIsStart(true);
            status.addBidInfoPerLevel(newBidOrder);
            status.updateUsedBalance(newBidOrder);
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

        if(status.getWaitAskOrder()) {
          Order oldAskOrder = status.getAskInfoPerLevel().get(level);
          Order newAskOrder = tradeJob.getOrder(selectedSymbol.getSymbol(), oldAskOrder.getOrderId());
          Double lossCutPrice = analysisJob.calLossCutPrice(status.getBidInfoPerLevel(), level, status.getUsedBalance());

          if( level == 5 && 
              !lastbidOrderTime.equals(candle.getTime()) &&
              candle.getClose().compareTo(lossCutPrice) == -1) {
            log.info("Loss cut.");
            log.info("[30 -> 999] [loss cut step] ");
            status.setStep(999);
          } else if(!lastbidOrderTime.equals(candle.getTime()) && 
                    candle.getFlag() == -1 &&
                    analysisJob.judgeScaleTrade(candle.getClose(), status.getBidPricePerLevel().get(level), level)) {
            log.info("[30 -> 42] [cancel ask order step] ");
            status.setStep(42);
          } else {
            if(newAskOrder.getStatus().equals(OrderState.FILLED)) {
              log.info("Success asking!!");
              log.info("[30 -> -1] [init step] ");
              status.setStep(-1);
            } else {
              log.info("Wait for ask");
            }
          }
        }
        break;

      case 40:
        // [ cancel bid order ]
        Order cancelBidOrder = tradeJob.cancelOrder(selectedSymbol.getSymbol(), status.getBidInfoPerLevel().get(level).getOrderId());
        if(cancelBidOrder.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel bid order!!");
          status.setWaitBidOrder(false);
          if(!status.getIsStart()) {
            log.info("[40 -> -1] [init step]");
            status.setStep(-1);
          } else {
            log.info("Cannot bid whild scale Trading. Roll back level!!");
            log.info("[40 -> 20] [ask step]");
            status.decreaseLevel();
            status.setStep(20);
          }
        } else {
          log.info("Error during cancelOrder");
        }
        break;      
      case 41:
        // [ cancel ask order for bid step ]
        Order cancelAskOrder = tradeJob.cancelOrder(selectedSymbol.getSymbol(), status.getAskInfoPerLevel().get(level).getOrderId());
        if(cancelAskOrder.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel ask order for bid!!");
          log.info("[41 -> 20] [ask step]");
          status.setWaitAskOrder(false);
          status.setStep(20);
        } else {
          log.info("Error during cancelOrder");
        }
        break;  
      case 42:
        // [ cancel ask order for scale trade step ]
        Order cancelAskOrderForScaleTrade = tradeJob.cancelOrder(selectedSymbol.getSymbol(), status.getAskInfoPerLevel().get(level).getOrderId());
        if(cancelAskOrderForScaleTrade.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel ask order for scale trade!!. Increase Level!!");
          log.info("[42 -> 10] [bid step]");
          status.increaseLevel();
          status.setWaitAskOrder(false);
          status.setStep(10);
        } else {
          log.info("Error during cancelOrder");
        }
        break; 
      case 999:   
        // [ loss cut step ]
        Order cancelOrderForLossCut = tradeJob.cancelOrder(selectedSymbol.getSymbol(), status.getAskInfoPerLevel().get(level).getOrderId());
        if(cancelOrderForLossCut.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel order!!");
          status.setWaitAskOrder(false);
          String quantity = analysisJob.getCoinQuantity(status.getBidInfoPerLevel(), level);
          Order newOrder = tradeJob.askMarket(selectedSymbol.getSymbol(), quantity);
          if(newOrder.getStatus().equals(OrderState.FILLED)) {
            log.info("Success loss cut..");
            log.info("[999 -> -1] [init step] ");
            status.setStep(-1);
          } else {
            log.info("Error during asking");

          }
        } else {
          log.info("Already success ask order!!");
          log.info("[999 -> -1] [init step] ");
          status.setStep(-1);
        }
        break; 
    }
    
  }
}
