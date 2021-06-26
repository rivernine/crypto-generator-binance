package com.rivernine.cryptoGeneratorBinance.schedule;

import java.math.BigDecimal;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Config;
import com.rivernine.cryptoGeneratorBinance.common.Status;
import com.rivernine.cryptoGeneratorBinance.schedule.analysis.AnalysisJob;
import com.rivernine.cryptoGeneratorBinance.schedule.market.MarketJob;
import com.rivernine.cryptoGeneratorBinance.schedule.trade.TradeJob;
import com.rivernine.cryptoGeneratorBinance.schedule.user.UserJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScalpWallJobScheduler {

  @Value("${binance.symbol}")
  public String symbol;

  private final Status status;
  private final Client client;
  private final Config config;
  
  private final MarketJob marketJob;
  private final AnalysisJob analysisJob;  
  private final UserJob userJob;
  private final TradeJob tradeJob;

  @Scheduled(fixedDelay = 500)
  public void runInit() {  
    if(!status.init) {
      client.init();              
      status.initScalping();  
      marketJob.setSymbolsInfo();   
      status.setPosition(true);     // Long
      status.setInit(true);
    }
    if(client.getQueryClient() == null || client.getInvokeClient() == null) {
      client.init();
    }  
  }

  @Scheduled(fixedDelay = 100)
  public void runScalping() {    
    switch(status.getStep()) {
      case -1:
        // init step
        status.initScalping();
        tradeJob.changeInitialLeverage(symbol, 1);
        break;
      case 0:
        // bid step
        // BigDecimal price = marketJob.getMarketPrice(symbol);
        // Order bidOrder = tradeJob.bid(symbol, "0.001", price.toString());
        Order bidOrder;
        if(status.getPosition()) {
          bidOrder = tradeJob.bidMarket(symbol, "0.003");
        } else {
          bidOrder = tradeJob.askMarket(symbol, "0.003");
        }
        log.info(bidOrder.toString());
        status.setBidOrder(bidOrder);
        status.setStep(1);
        break;
      case 1:
        // wait step(bid)
        Order waitBidOrder = tradeJob.getOrder(symbol, status.getBidOrder().getOrderId());
        if(waitBidOrder.getStatus().equals("FILLED")) {
          log.info("Success bidding!!");      
          status.setStep(2);
          return;
        } else {
          log.info("Wait for bid");
          status.setWaitCount(status.getWaitCount() + 1);
          if(status.getWaitCount() > 5) {
            log.info("Cancel bid");
            status.setStep(10);
            return;
          } 
        }
        break;
      case 2:
        // ask step
        if(status.getAvgBuyPrice() == null) {
          status.setAvgBuyPrice(userJob.getEntryPrice(symbol));
        }
        BigDecimal coinQuantity = status.getBidOrder().getOrigQty();
        String askPrice = analysisJob.calAskPriceForScalping(status.getSymbolsInfo().get(symbol), status.getAvgBuyPrice(), status.getPosition());
        Order askOrder;
        if(status.getPosition()) {
          askOrder = tradeJob.ask(symbol, coinQuantity.toString(), askPrice);
        } else {
          askOrder = tradeJob.bid(symbol, coinQuantity.toString(), askPrice);
        }
        log.info(askOrder.toString());
        status.setAskOrder(askOrder);
        status.setStep(3);
        break;
      case 3:
        // wait step(ask)
        Order waitAskOrder = tradeJob.getOrder(symbol, status.getAskOrder().getOrderId());
        if(waitAskOrder.getStatus().equals("FILLED")) {
          log.info("Success asking!!");   
          log.info("++++++++++Generated++++++++++");
          status.setStep(-1);
          return;
        }

        BigDecimal curPrice = marketJob.getMarketPrice(symbol);
        BigDecimal lossCutPrice = analysisJob.calLossCutPriceForScalping(status.getAvgBuyPrice(), status.getPosition());
        if(status.getPosition() && curPrice.compareTo(lossCutPrice) == -1) {
          log.info("curPrice : lossCutPrice");
          log.info(curPrice.toString() + " : " + lossCutPrice.toString());
          log.info("----------Loss cut----------");
          status.setStep(11);
        } else if(!status.getPosition() && curPrice.compareTo(lossCutPrice) == 1) {
          log.info("curPrice : lossCutPrice");
          log.info(curPrice.toString() + " : " + lossCutPrice.toString());
          log.info("----------Loss cut----------");
          status.setStep(11);
        }
        break;
      case 10:
        // cancel step(bid)
        try{
          Order cancelBidOrder = tradeJob.cancelOrder(symbol, status.getBidOrder().getOrderId());
          log.info(cancelBidOrder.toString());
          if(cancelBidOrder.getStatus().equals("CANCELED")) {
            log.info("Success cancel bid order!!");
            status.setStep(-1);
          }
        } catch( Exception e ) {
          log.info(e.getMessage());
          log.info("Already bidding");
          status.setStep(2);
        }
        break;
      case 11:
        // losscut step
        try{
          Order cancelAskOrder = tradeJob.cancelOrder(symbol, status.getAskOrder().getOrderId());
          log.info(cancelAskOrder.toString());
          if(cancelAskOrder.getStatus().equals("CANCELED")) {
            log.info("Success cancel ask order!!");
            BigDecimal quantity = status.getBidOrder().getOrigQty();
            Order newOrder;
            if(status.getPosition()) {
              newOrder = tradeJob.askMarket(symbol, quantity.toString());
              log.info("Change position. (Long -> Short)");
            } else {
              newOrder = tradeJob.bidMarket(symbol, quantity.toString());
              log.info("Change position. (Short -> Long)");
            }
            log.info(newOrder.toString());
            status.setPosition(!status.getPosition());
          }
        } catch( Exception e ) {
          log.info("++++++++++Generated++++++++++");
          log.info(e.getMessage());
        } finally {
          status.setStep(-1);
        }
        
        break;
    }

  }
}
