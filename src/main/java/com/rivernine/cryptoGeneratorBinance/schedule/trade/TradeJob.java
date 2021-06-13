package com.rivernine.cryptoGeneratorBinance.schedule.trade;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Leverage;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.schedule.trade.impl.TradeImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TradeJob {
  private final TradeImpl tradeImpl;

  public Leverage changeInitialLeverage(String symbol, Integer leverage) {
    return tradeImpl.changeInitialLeverage(symbol, leverage);
  }

  public Order getOrder(String symbol, Long orderId) {
    return tradeImpl.getOrder(symbol, orderId);
  }

  public Order bid(String symbol, String quantity, String price) {
    return tradeImpl.bid(symbol, quantity, price);
  }

  public Order ask(String symbol, String quantity, String price) {
    return tradeImpl.ask(symbol, quantity, price);
  }

  public Order askMarket(String symbol, String quantity) {
    return tradeImpl.askMarket(symbol, quantity);
  }

  public Order cancelOrder(String symbol, Long orderId) { 
    return tradeImpl.cancelOrder(symbol, orderId);
  }
}
