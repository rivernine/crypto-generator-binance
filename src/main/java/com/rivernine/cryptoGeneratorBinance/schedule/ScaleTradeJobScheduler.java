package com.rivernine.cryptoGeneratorBinance.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Leverage;
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
    BigDecimal usedBalance;

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
        // status.setSymbol(status.getSymbolsInfo().get("XRPUSDT"));
        // status.setStep(10);
        break;
      case 10:
        // [bid step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        Integer leverage = config.getLeveragePerLevel(level);
        BigDecimal myBalance = userJob.getUSDTBalance().getBalance();
        BigDecimal bidBalance = config.getBidBalance().multiply(new BigDecimal(leverage));

        if(myBalance.compareTo(bidBalance) != -1) {
          BigDecimal closePrice = candle.getClose();
          String quantity = analysisJob.convertStepSize(symbol, bidBalance.divide(closePrice, 8, RoundingMode.UP));
          Leverage res = tradeJob.changeInitialLeverage(symbolName, leverage);
          log.info(res.toString());
          bidOrder = tradeJob.bid(symbolName, quantity, closePrice.toString());
          log.info(bidOrder.toString());
          log.info("[10 -> 30] [wait step]");
          status.addBidInfoPerLevel(bidOrder);
          status.addBidPricePerLevel(closePrice);
          status.setBidOrderTime(candle.getTime());
          status.setWaitBidOrder(true);
          status.setStep(30);
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
        log.info(askOrder.toString());
        log.info("[20 -> 30] [wait step]");
        status.addAskInfoPerLevel(askOrder);
        status.setWaitAskOrder(true);
        status.setStep(30);
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

          log.info(newBidOrder.toString());
          if(newBidOrder.getStatus().equals("FILLED")) {
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
          BigDecimal lossCutPrice = analysisJob.calLossCutPrice(coinQuantity, usedBalance);

          log.info(newAskOrder.toString());
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
            if(newAskOrder.getStatus().equals("FILLED")) {
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

        log.info(cancelBidOrder.toString());
        if(cancelBidOrder.getStatus().equals("CANCELED")) {
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

        log.info(cancelAskOrder.toString());
        if(cancelAskOrder.getStatus().equals("CANCELED")) {
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

        log.info(cancelAskOrderForScaleTrade.toString());
        if(cancelAskOrderForScaleTrade.getStatus().equals("CANCELED")) {
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

        if(cancelOrderForLossCut.getStatus().equals("CANCELED")) {
          log.info("Success cancel order!!");
          status.setWaitAskOrder(false);
          String quantity = userJob.getCoinQuantity(bidOrders, level);
          Order newOrder = tradeJob.askMarket(symbolName, quantity);
          if(newOrder.getStatus().equals("FILLED")) {
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
