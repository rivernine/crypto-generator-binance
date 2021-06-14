package com.rivernine.cryptoGeneratorBinance.schedule;

import java.util.HashMap;
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

  @Scheduled(fixedDelay = 1000)
  public void runCollectCandlesJob() {    
    List<String> symbols = status.getSymbols();
    // log.info(symbols.toString());
    for(String symbol: symbols) {
      marketJob.collectCandlesFiveMinutes(symbol, 4);
    }
  }

  @Scheduled(fixedDelay = 1000)
  public void runScaleTradeJob() {
    Integer level = status.getLevel();
    List<Candle> candles;
    Candle candle;
    Map<Integer, Order> bidOrders = status.getBidInfoPerLevel();
    Map<Integer, Order> askOrders = status.getAskInfoPerLevel();
    Order bidOrder, askOrder;
    Symbol symbol = status.getSymbol();
    String symbolName;
    String coinQuantity;
    Double usedBalance;

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
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        Integer leverage = config.getLeveragePerLevel(level);
        Double myBalance = userJob.getUSDTBalance().getBalance();
        Double bidBalance = config.getBidBalance() * leverage.doubleValue();

        if(myBalance.compareTo(bidBalance) != -1) {
          Double closePrice = candle.getClose();
          String quantity = analysisJob.convertStepSize(symbol, bidBalance / closePrice);
          tradeJob.changeInitialLeverage(symbolName, leverage);
          bidOrder = tradeJob.bid(symbolName, quantity, closePrice.toString());
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
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        coinQuantity = userJob.getCoinQuantity(bidOrders, level);
        usedBalance = status.getUsedBalance();
        String askPrice = analysisJob.calAskPrice(level, symbol, coinQuantity, usedBalance);
        askOrder = tradeJob.ask(symbolName, coinQuantity, askPrice);
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
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        String lastbidOrderTime = status.getBidOrderTime();
        
        if(status.getWaitBidOrder()) {
          if(!lastbidOrderTime.equals(candle.getTime())) {
            if(!status.getIsStart()) 
              log.info("Must find another chance..");
            log.info("[30 -> 40] [cancel bid step] ");
            status.setStep(40);
            break;
          }

          Order oldBidOrder = bidOrders.get(level);
          Order newBidOrder = tradeJob.getOrder(symbolName, oldBidOrder.getOrderId());
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
          Order newAskOrder = tradeJob.getOrder(symbolName, oldAskOrder.getOrderId());
          coinQuantity = userJob.getCoinQuantity(bidOrders, level);
          usedBalance = status.getUsedBalance();
          Double lossCutPrice = analysisJob.calLossCutPrice(coinQuantity, usedBalance);

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
              log.info("[30 -> 0] [init step] ");
              status.setStep(0);
            } else {
              log.info("Wait for ask");
            }
          }
        }
        break;

      case 40:
        // [ cancel bid order ]
        symbolName = symbol.getSymbolName();
        Order cancelBidOrder = tradeJob.cancelOrder(symbolName, bidOrders.get(level).getOrderId());
        if(cancelBidOrder.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel bid order!!");
          status.setWaitBidOrder(false);
          if(!status.getIsStart()) {
            log.info("[40 -> 0] [init step]");
            status.setStep(0);
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
        symbolName = symbol.getSymbolName();
        Order cancelAskOrder = tradeJob.cancelOrder(symbolName, askOrders.get(level).getOrderId());
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
        symbolName = symbol.getSymbolName();
        Order cancelAskOrderForScaleTrade = tradeJob.cancelOrder(symbolName, askOrders.get(level).getOrderId());
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
        symbolName = symbol.getSymbolName();
        Order cancelOrderForLossCut = tradeJob.cancelOrder(symbolName, askOrders.get(level).getOrderId());
        if(cancelOrderForLossCut.getStatus().equals(OrderState.CANCELED)) {
          log.info("Success cancel order!!");
          status.setWaitAskOrder(false);
          String quantity = userJob.getCoinQuantity(bidOrders, level);
          Order newOrder = tradeJob.askMarket(symbolName, quantity);
          if(newOrder.getStatus().equals(OrderState.FILLED)) {
            log.info("Success loss cut..");
            log.info("[999 -> 0] [init step] ");
            status.setStep(0);
          } else {
            log.info("Error during asking");

          }
        } else {
          log.info("Already success ask order!!");
          log.info("[999 -> 0] [init step] ");
          status.setStep(0);
        }
        break; 
    }
    
  }
}
