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
public class ScalpJobScheduler {

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
    BigDecimal price = marketJob.getMarketPrice(symbol);
      
    if(!status.getBidding()){ 
      Order bidOrder = tradeJob.bid(symbol, "0.001", price.toString());
      log.info(bidOrder.toString());
      status.setBidOrder(bidOrder);
      status.setBidding(true);
      status.setWaiting(true);
    }
    
    if(status.getBidding() && status.getWaiting()) {
      Long orderId = status.getBidOrder().getOrderId();
      Order order = tradeJob.getOrder(symbol, orderId);
      if(order.getStatus().equals("FILLED")) {
        log.info("Success bidding!!");      
        status.setWaiting(false);   
        status.setAskTrigger(true);
      } else {
        log.info("Wait for bid");
        status.setWaitCount(status.getWaitCount() + 1);
        if(status.getWaitCount() > 5) {
          Order cancelBidOrder = tradeJob.cancelOrder(symbol, orderId);
          log.info(cancelBidOrder.toString());

          if(cancelBidOrder.getStatus().equals("CANCELED")) {
            log.info("Success cancel bid order!!");
            status.initScalping();
          } else {
            log.info("Already bidding");
          }
          return;
        } 
      }
    }

    if(status.getAskTrigger()) {
      BigDecimal avgBuyPrice = userJob.getEntryPrice(symbol);
      BigDecimal coinQuantity = status.getBidOrder().getOrigQty();
      String askPrice = analysisJob.calAskPriceForScalping(status.getSymbolsInfo().get(symbol), avgBuyPrice);

      Order askOrder = tradeJob.ask(symbol, coinQuantity.toString(), askPrice);
      log.info(askOrder.toString());
      status.seAskOrder(askOrder);
      status.setBidding(true);
      status.setWaiting(true);
    }

        
    
  }
}
