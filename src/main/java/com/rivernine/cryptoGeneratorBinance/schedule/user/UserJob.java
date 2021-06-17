package com.rivernine.cryptoGeneratorBinance.schedule.user;

import java.math.BigDecimal;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;
import com.rivernine.cryptoGeneratorBinance.schedule.user.impl.UserImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserJob {
  private final UserImpl userImpl;

  public Balance getUSDTBalance() {
    return userImpl.getUSDTBalance();
  }

  public String getCoinQuantity(Map<Integer, Order> bidOrders, Integer level) {
    return userImpl.getCoinQuantity(bidOrders, level);
  }

  public BigDecimal getEntryPrice(String symbolName) {
    return userImpl.getEntryPrice(symbolName);
  }
}
