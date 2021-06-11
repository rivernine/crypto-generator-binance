package com.rivernine.cryptoGeneratorBinance.schedule.trade.impl;

import java.math.BigDecimal;

import com.rivernine.cryptoGeneratorBinance.client.model.enums.NewOrderRespType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.PositionSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.TimeInForce;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.WorkingType;
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

  public void bid(String symbol, String quantity, String price) {
    // bid
    client.getInvokeClient().postOrder("XRPUSDT", OrderSide.BUY, PositionSide.LONG, OrderType.LIMIT,
                                        TimeInForce.FOK, "6", "1000.0", null,
                                        null, "1010.0", null, NewOrderRespType.RESULT);
    // client.getInvokeClient().
  }

}
