package com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.common.Status;
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
  private Double longBlueCandleRate;
  @Value("${binance.lossCutRate}")
  private Double lossCutRate;
  @Value("${binance.marginRatePerLevel}")	
  private List<Double> marginRatePerLevel;
  @Value("${binance.scaleTradeRatePerLevel}")	
  private List<Double> scaleTradeRatePerLevel;

  private final Status status;

  public Boolean analysisCandles(List<Candle> candles, Integer count) {
    Boolean result = false;
    if(candles.size() < count) {
      log.info("Not enough size. Candles size: " + Integer.toString(candles.size()));
      result = false;
    } else {
      if(candles.get(0).getFlag() != -1) {
        log.info("Last flag is not -1. return false");
        return false;
      }
      int longBlueCandleCount = 0;
      Double minPrice = 100000000.00000000;
      Double maxPrice = 0.0;

      for(Candle candle: candles) {
        log.info(candle.toString());
        if(candle.getFlag() == 1) {
          log.info("getFlag == 1. Return false");
          return false;
        }
        maxPrice = Double.max(maxPrice, candle.getOpen());
        minPrice = Double.min(minPrice, candle.getClose());
        Double thresholdPrice = candle.getOpen() * (1 - longBlueCandleRate);
        Double thresholdPrice2 = maxPrice * (1 - (longBlueCandleRate * 2));

        log.info("threshold(two longBlueCandle) : " + thresholdPrice);
        log.info("threshold(longlongBlueCandle) : " + thresholdPrice2);
        if(candle.getClose().compareTo(thresholdPrice) != 1)
          longBlueCandleCount += 1;
        if(minPrice.compareTo(thresholdPrice2) != 1)
          return true;
        if(longBlueCandleCount >= 2)
          return true;
      }
    }

    return result;
  }

  public String calAskPrice(Symbol symbol, Map<Integer, Order> bidOrders, Integer level, Double usedBalance) {
    Double feeRate = 0.0002;
    Double marginRate = marginRatePerLevel.get(level);
    Double coinQuantity = Double.parseDouble(getCoinQuantity(bidOrders, level));
    Double targetBalance = usedBalance * (1 + marginRate + feeRate);
    Double targetPrice = targetBalance / coinQuantity;

    targetPrice = convertTickPrice(symbol, targetPrice);
    log.info("coinQuantity : targetBalance");
    log.info(coinQuantity.toString() + " : " + targetBalance.toString());

    return targetPrice.toString();
  }

  public Double calLossCutPrice(Map<Integer, Order> bidOrders, Integer level, Double usedBalance) {
    Double coinQuantity = Double.parseDouble(getCoinQuantity(bidOrders, level));
    Double avgBuyPrice = usedBalance / coinQuantity;
    Double lossCutPrice = avgBuyPrice * (1 - lossCutRate);

    return lossCutPrice;
  }

  public String getCoinQuantity(Map<Integer, Order> bidOrders, Integer level) {
    Double coinQuantity = 0.0;
    for(int i = 1; i <= level; i++) {
      coinQuantity += bidOrders.get(i).getOrigQty().doubleValue();
    }

    return coinQuantity.toString();
  }

  public Boolean judgeScaleTrade(Double curPrice, Double lastBidPrice, Integer level) {
    Boolean result;
    Double scaleTradeRate = scaleTradeRatePerLevel.get(level);
    Double thresholdPrice = lastBidPrice * (1 - scaleTradeRate);
    
    if(curPrice.compareTo(thresholdPrice) == -1) {
      result = true;
    } else {
      result = false;
    }
    log.info("curPrice : thresholdPrice");
    log.info(curPrice.toString() + " : " + thresholdPrice.toString());

    return result;
  }

  public Double convertTickPrice(Symbol symbol, Double price) {
    Double result;
    Double tickSize = Double.parseDouble(symbol.getTickSize());
    Double mod = price % tickSize;

    if(mod.compareTo(0.0) == 0) {
      result = price;
    } else {
      Double tmp = price / tickSize;
      result = tmp.intValue() * tickSize + tickSize;
    }
    log.info("price : convertedPrice");
    log.info(price.toString() + " : " + result.toString());

    return result;
  }

  public String convertStepSize(Symbol symbol, Double quantity) {
    Double result;
    Double stepSize = Double.parseDouble(symbol.getStepSize());
    Double mod = quantity / stepSize;

    if(mod.compareTo(0.0) == 0) {
      result = quantity;
    } else {
      Double tmp = quantity / stepSize;
      result = tmp.intValue() * stepSize;
    }

    return result.toString();
  }
}
