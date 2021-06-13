package com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl;

import java.util.List;
import java.util.Map;

import com.rivernine.cryptoGeneratorBinance.client.model.trade.Order;
import com.rivernine.cryptoGeneratorBinance.schedule.market.dto.Candle;

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
  @Value("${binance.marginRatePerLevel}")	
  private List<Double> marginRatePerLevel;

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

  public String calAskPrice(Map<Integer, Order> bidOrders, Integer level, Double usedBalance) {
    Double feeRate = 0.0002;
    Double marginRate = marginRatePerLevel.get(level);

    Double coinQuantity = 0.0;
    for(int i = 1; i <= level; i++) {
      coinQuantity += bidOrders.get(i).getOrigQty().doubleValue();
    }

    Double targetBalance = usedBalance * (1 + marginRate + feeRate);
    Double targetPrice = targetBalance / coinQuantity;
    targetPrice = change

    Double targetBalance = Double.parseDouble(totalUsedBalance) * (1 + marginRate + feeRate);
    String targetPrice = Double.toString(targetBalance / Double.parseDouble(coinBalance));
    String targetPriceAbleOrder = changeAbleOrderPrice(targetPrice);

    log.info("level : marginRate");
    log.info(Integer.toString(scaleTradeStatusProperties.getLevel()) + " : " + Double.toString(marginRate));
    log.info("usedBalance : usedFee : totalUsedBalance");
    log.info(usedBalance + " : " + usedFee + " : " + totalUsedBalance);
    log.info("coinBalance : targetPrice : targetPriceAbleOrder");
    log.info(coinBalance + " : " + targetPrice + " : " + targetPriceAbleOrder);

    return targetPriceAbleOrder;

  }
}
