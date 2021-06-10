package com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl;

import java.util.List;

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
}
