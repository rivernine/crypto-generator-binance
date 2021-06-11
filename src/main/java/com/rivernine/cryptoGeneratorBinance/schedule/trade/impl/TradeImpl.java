package com.rivernine.cryptoGeneratorBinance.schedule.trade.impl;

import com.rivernine.cryptoGeneratorBinance.client.model.enums.NewOrderRespType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.PositionSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.TimeInForce;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.Leverage;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.common.Status;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TradeImpl {

  private final Status status;
  private final Client client;

  public Leverage changeInitialLeverage(String symbol, Integer leverage) {
    return client.getInvokeClient().changeInitialLeverage(symbol, leverage);
  }

  public Order bid(String symbol, String quantity, String price) {
    return client.getInvokeClient().postOrder(symbol, OrderSide.BUY, PositionSide.BOTH, OrderType.LIMIT,
                                      TimeInForce.GTC, quantity, price, null,
                                      null, null, null, NewOrderRespType.RESULT);
  }

  public Order getOrder(String symbol, Long orderId) {
    return client.getInvokeClient().getOrder(symbol, orderId, null);
  }

}
