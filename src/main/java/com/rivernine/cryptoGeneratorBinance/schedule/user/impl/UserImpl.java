package com.rivernine.cryptoGeneratorBinance.schedule.user.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.AccountBalance;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.PositionRisk;
import com.rivernine.cryptoGeneratorBinance.common.Client;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserImpl {

  private final Client client;

  public Balance getUSDTBalance() {
    List<AccountBalance> balances = client.getInvokeClient().getBalance();
    Balance result = new Balance();
    for(AccountBalance balance: balances) {
      if(balance.getAsset().equals("USDT")) {
        result = Balance.builder() 
                        .asset(balance.getAsset())
                        .balance(balance.getBalance())
                        .withdrawAvailable(balance.getWithdrawAvailable())
                        .build();
      }
    }
    return result;
  }

  public String getCoinQuantity(Map<Integer, Order> bidOrders, Integer level) {
    BigDecimal coinQuantity = new BigDecimal(0.0);
    for(int i = 1; i <= level; i++) {
      coinQuantity = coinQuantity.add(bidOrders.get(i).getOrigQty());
    }

    return coinQuantity.toString();
  }

  public BigDecimal getEntryPrice(String symbolName) {
    BigDecimal result = new BigDecimal(-1);
    List<PositionRisk> positions = client.getInvokeClient().getPositionRisk();
    
    for(PositionRisk position: positions) {
      if(position.getSymbol().equals(symbolName)) {
        log.info(position.toString());
        result = position.getEntryPrice();
      }
    }

    return result;
  }

}  
