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
public class TradeFiveMinutes {

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
      // marketJob.collectCandlesFiveMinutes(symbol, 4);
      marketJob.collectCandlesThreeMinutes(symbol, 4);
    }
  }

  @Scheduled(fixedDelay = 500)
  public void runTradeFiveMinutes() {
    List<Candle> candles;
    Candle candle;
    Order bidOrder, askOrder;
    Symbol symbol = status.getSymbol();
    String symbolName = null;
    String quantity;
    BigDecimal bidBalance;
    switch(status.getStep()) {
      case -1:
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        if(!status.getBidOrderTime().equals(candle.getTime())) {
          status.setIsLossCut(false);
          log.info("[-1 -> 0] [init step] ");
          status.setStep(0);
        }
        break;
      case 0:  
        // [ init step ]
        log.info("[0 -> 1] [select market step] ");
        status.init();
        status.initScalping();
        client.init();
        marketJob.setSymbolsInfo();
        status.setStep(1);
        break;
      case 1:
        // [ select market step ]
        // log.info("---------------------");
        for(String symb: status.getSymbols()) {
          // log.info("< " + symb + " >");
          candles = marketJob.getRecentCandles(symb, 3);
          if(analysisJob.analysisCandles(candles, 3)) {
            log.info("It's time to bid!! My select : " + symb);
            log.info("[1 -> 10] [bid step] ");
            Symbol select = status.getSymbolsInfo().get(symb);
            Leverage res = tradeJob.changeInitialLeverage(symb, 1);
            log.info(res.toString());
            status.setSymbol(select);
            status.setStep(10);
            break;
          }
        }
        break;
      case 10:
        // [bid step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        bidBalance = config.getBidBalance();

        BigDecimal closePrice = candle.getClose();
        BigDecimal marketPrice = marketJob.getMarketPrice(symbolName);
        BigDecimal price = closePrice.min(marketPrice);
        quantity = analysisJob.convertStepSize(symbol, bidBalance.divide(price, 8, RoundingMode.UP));
        
        bidOrder = tradeJob.bid(symbolName, quantity, price.toString());
        log.info(bidOrder.toString());
        status.setBidOrder(bidOrder);
        status.setBidOrderTime(candle.getTime());
        status.setWaitBidOrder(true);
        log.info("[10 -> 30] [wait step]");
        status.setStep(30);
        break;
      case 20:
        // [ ask step ]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        BigDecimal avgBuyPrice = userJob.getEntryPrice(symbolName);
        BigDecimal coinQuantity = status.getBidOrder().getOrigQty();
        // String askPrice = analysisJob.calAskPrice(level, symbol, avgBuyPrice);
        String askPrice = analysisJob.calAskPriceForScalping(symbol, avgBuyPrice, true);
        askOrder = tradeJob.ask(symbolName, coinQuantity.toString(), askPrice);
        log.info(askOrder.toString());
        log.info("[20 -> 30] [wait step]");
        status.setAskOrder(askOrder);
        status.setWaitAskOrder(true);
        status.setStep(30);
        break;
      case 30:
        symbolName = symbol.getSymbolName();
        candle = marketJob.getLastCandle(symbolName);
        String lastbidOrderTime = status.getBidOrderTime();
        
        if(status.getWaitBidOrder()) {
          if(!lastbidOrderTime.equals(candle.getTime())) {
            log.info("[30 -> 40] [cancel bid step] ");
            status.setStep(40);
            break;
          }
          Order oldBidOrder = status.getBidOrder();
          Order newBidOrder = tradeJob.getOrder(symbolName, oldBidOrder.getOrderId());
          if(newBidOrder.getStatus().equals("FILLED")) {
            log.info("Success bidding!!");              
            log.info(newBidOrder.toString());
            status.setBidOrder(newBidOrder);
            status.setWaitBidOrder(false);
            log.info("[30 -> 20] [ask step] ");
            status.setStep(20);
          }
        }

        if(status.getWaitAskOrder()) {
          Order oldAskOrder = status.getAskOrder();
          Order newAskOrder = tradeJob.getOrder(symbolName, oldAskOrder.getOrderId());
          avgBuyPrice = userJob.getEntryPrice(symbolName);
          BigDecimal lossCutPrice = analysisJob.calLossCutPrice(avgBuyPrice);
          BigDecimal currentPrice = marketJob.getMarketPrice(symbolName);

          if(currentPrice.compareTo(lossCutPrice) == -1) {
            log.info("Loss cut.");
            log.info("[30 -> 999] [loss cut step] ");
            status.setStep(999);
          } else {
            if(newAskOrder.getStatus().equals("FILLED")) {
              log.info("Success asking!!");
              log.info("[30 -> 0] [init step] ");
              status.setStep(0);
            }
          }
        }
        break;

      case 40:
        // [ cancel bid order ]
        symbolName = symbol.getSymbolName();
        try{
          Order cancelBidOrder = tradeJob.cancelOrder(symbolName, status.getBidOrder().getOrderId());
          log.info(cancelBidOrder.toString());
          log.info("Success cancel bid order!!");
          log.info("[40 -> 0] [init step]");
          status.setStep(0);
        } catch(Exception e) {
          log.info(e.getMessage());
          log.info("Already bidding");
          log.info("[40 -> 20] [ask step]");
          status.setStep(20);
        } finally {
          status.setWaitBidOrder(false);
        } 
        break;      
      case 999:   
        // [ loss cut step ]
        symbolName = symbol.getSymbolName();
        try{
          Order cancelOrderForLossCut = tradeJob.cancelOrder(symbolName, status.getAskOrder().getOrderId());
          log.info(cancelOrderForLossCut.toString());
          log.info("Success loss cut..");
          Order newOrder = tradeJob.askMarket(symbolName, status.getAskOrder().getOrigQty().toString());
          log.info(newOrder.toString());
          log.info("[999 -> 1000] [short step] ");
          status.setIsLossCut(true);
          status.setStep(1000);
        } catch(Exception e) {
          log.info(e.getMessage());
        }
        break; 
      case 1000:
        // [short step]
        // [bid step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        bidBalance = config.getBidBalance();
        quantity = analysisJob.convertStepSize(symbol, bidBalance.divide(marketJob.getMarketPrice(symbolName), 8, RoundingMode.UP));
        bidOrder = tradeJob.askMarket(symbolName, quantity);
        log.info(bidOrder.toString());
        status.setBidOrder(bidOrder);
        log.info("[1000 -> 1001] [wait step] ");
        status.setStep(1001);
        break;
      case 1001:
        // [wait step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        Order waitBidOrder = tradeJob.getOrder(symbolName, status.getBidOrder().getOrderId());
        if(waitBidOrder.getStatus().equals("FILLED")) {
          log.info("Success bidding!![short]");      
          log.info("[1001 -> 1002] [wait step] ");
          status.setStep(1002);
        }
        break;
      case 1002:
        // [wait for red candle step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        candle = marketJob.getCandleOneMinute(symbolName);
        if(candle.getFlag() == 1) {
          log.info("It's time to ask");
          log.info("[1002 -> 1003] [ask step] ");
          status.setStep(1003);
        }
        break;
      case 1003:
        // [ask step]
        symbol = status.getSymbol();
        symbolName = symbol.getSymbolName();
        quantity = status.getBidOrder().getOrigQty().toString();
        Order newOrder = tradeJob.bidMarket(symbolName, quantity.toString());
        log.info(newOrder.toString());
        log.info("Success asking!! [short]");
        log.info("[1003 -> -1] [before step] ");
        status.setStep(-1);
        break;
    }
    
  }
}
