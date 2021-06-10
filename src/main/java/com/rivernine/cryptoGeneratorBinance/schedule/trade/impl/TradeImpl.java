package com.rivernine.cryptoGeneratorBinance.schedule.trade.impl;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.model.market.ExchangeInfoEntry;
import com.rivernine.cryptoGeneratorBinance.client.model.market.ExchangeInformation;
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

  public void sample() {
    ExchangeInformation exchangeInfo = client.getQueryClient().getExchangeInformation();
    List<ExchangeInfoEntry> symbols = exchangeInfo.getSymbols();
    for(ExchangeInfoEntry symbol: symbols) {
      List<List<Map<String, String>>> filters = symbol.getFilters();      
      String tickSize = null;
      String stepSize = null;
      for(List<Map<String, String>> filter: filters) {
        for(Map<String, String> obj: filter) {
          if(obj.containsKey("tickSize")) {
            tickSize = obj.get("tickSize");
          } else if(obj.containsKey("stepSize")) {
            stepSize = obj.get("stepSize");
          }
        }
      }

      log.info("Symbol: " + symbol.getSymbol() + ", tickSize: " + tickSize + ", stepSize: " + stepSize);
    }
  }

}

// [
//   [
//     {minPrice=0.000129}, 
//     {maxPrice=100000}, 
//     {filterType=PRICE_FILTER}, 
//     {tickSize=0.000001}
//   ], 
//   [
//     {stepSize=1},
//     {filterType=LOT_SIZE}, 
//     {maxQty=10000000}, 
//     {minQty=1}
//   ], 
//   [
//     {stepSize=1}, 
//     {filterType=MARKET_LOT_SIZE}, 
//     {maxQty=5000000}, 
//     {minQty=1}
//   ], 
//   [
//     {limit=200}, 
//     {filterType=MAX_NUM_ORDERS}
//   ], 
//   [
//     {limit=10}, 
//     {filterType=MAX_NUM_ALGO_ORDERS}
//   ], 
//   [
//     {notional=5}, 
//     {filterType=MIN_NOTIONAL}
//   ], 
//   [
//     {multiplierDown=0.8500}, 
//     {multiplierUp=1.1500}, 
//     {multiplierDecimal=4}, 
//     {filterType=PERCENT_PRICE}
//   ]
// ]