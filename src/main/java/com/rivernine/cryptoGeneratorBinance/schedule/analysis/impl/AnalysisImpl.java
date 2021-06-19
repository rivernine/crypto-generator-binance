package com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.model.market.OrderBook;
import com.rivernine.cryptoGeneratorBinance.client.model.market.OrderBookEntry;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Symbol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnalysisImpl {

  @Value("${binance.longBlueCandleRate}")
  private BigDecimal longBlueCandleRate;
  @Value("${binance.lossCutRate}")
  private BigDecimal lossCutRate;
  @Value("${binance.marginRatePerLevel}")	
  private List<BigDecimal> marginRatePerLevel;
  @Value("${binance.scaleTradeRatePerLevel}")	
  private List<BigDecimal> scaleTradeRatePerLevel;

  // scalping
  // public List<BigDecimal> getWall(OrderBook orderBook) {
  //   // [0]: askWall, [1]: bidWall
  //   List<BigDecimal> result = new ArrayList<>();
    
  //   List<OrderBookEntry> asks = orderBook.getAsks();  // 오름차순
  //   List<OrderBookEntry> bids = orderBook.getBids();  // 내림차순

  //   for(OrderBookEntry entry: asks) {
  //     BigDecimal balance = entry.getPrice().multiply(entry.getQty());
  //     // if(balance)
  //   }
  // }

  // scale trade

  public Boolean analysisCandles(List<Candle> candles, Integer count) {
    Boolean result = false;
    if(candles.size() < count) {
      log.info("Not enough size. Candles size: " + Integer.toString(candles.size()));
      result = false;
    } else {
      if(candles.get(0).getFlag() != -1) {
        log.info("Last flag is not -1. Return false");
        return false;
      }
      BigDecimal totalChange = new BigDecimal(0.0);

      // candle은 최신순
      for(Candle candle: candles) {
        log.info(candle.toString());
        totalChange = totalChange.add(candle.getOpen().divide(candle.getClose(), 8, RoundingMode.HALF_UP).subtract(new BigDecimal(1)));
        log.info("totalChange : " + totalChange.toString());

        if(totalChange.compareTo(longBlueCandleRate) != -1)
          return true;
      }
    }

    return result;
  }

  public String calAskPrice(Integer level, Symbol symbol, BigDecimal avgBuyPrice) {
    BigDecimal feeRate = new BigDecimal(0.0002);
    BigDecimal marginRate = marginRatePerLevel.get(level - 1);
    BigDecimal targetPrice = avgBuyPrice.multiply(marginRate.add(feeRate).add(new BigDecimal(1)));

    targetPrice = convertTickPrice(symbol, targetPrice);
    log.info("avgBuyPrice : targetPrice");
    log.info(avgBuyPrice.toString() + " : " + targetPrice.toString());

    return targetPrice.toString();
  }

  public BigDecimal calLossCutPrice(BigDecimal avgBuyPrice) {
    return avgBuyPrice.multiply(new BigDecimal(1).subtract(lossCutRate));
  }

  public Boolean judgeScaleTrade(BigDecimal curPrice, BigDecimal lastBidPrice, Integer level) {
    Boolean result;
    BigDecimal scaleTradeRate = scaleTradeRatePerLevel.get(level - 1);
    BigDecimal thresholdPrice = lastBidPrice.multiply(new BigDecimal(1).subtract(scaleTradeRate));
    
    if(curPrice.compareTo(thresholdPrice) == -1) {
      result = true;
    } else {
      result = false;
    }
    log.info("curPrice : thresholdPrice");
    log.info(curPrice.toString() + " : " + thresholdPrice.toString());

    return result;
  }

  public BigDecimal convertTickPrice(Symbol symbol, BigDecimal price) {
    BigDecimal result;
    BigDecimal tickSize = new BigDecimal(symbol.getTickSize());
    BigDecimal mod = price.remainder(tickSize);

    if(mod.compareTo(new BigDecimal(0.0)) == 0) {
      result = price;
    } else {
      BigDecimal tmp = price.divide(tickSize, 8, RoundingMode.HALF_UP);
      result = tmp.setScale(0, RoundingMode.DOWN).multiply(tickSize);
      result = result.add(tickSize);
    }

    return result;
  }

  public String convertStepSize(Symbol symbol, BigDecimal quantity) {
    BigDecimal result;
    BigDecimal stepSize = new BigDecimal(symbol.getStepSize());
    BigDecimal mod = quantity.remainder(stepSize);

    if(mod.compareTo(new BigDecimal(0.0)) == 0) {
      result = quantity;
    } else {
      BigDecimal tmp = quantity.divide(stepSize, 8, RoundingMode.HALF_UP);
      result = tmp.setScale(0, RoundingMode.DOWN).multiply(stepSize);
      if(result.compareTo(new BigDecimal(5.0)) == -1)
        result = result.add(stepSize);
    }

    log.info("stepSize : quantity : convertedQuantity");
    log.info(stepSize.toString() + " : " + quantity.toString() + " : " + result.toString());

    return result.toString();
  }
}
