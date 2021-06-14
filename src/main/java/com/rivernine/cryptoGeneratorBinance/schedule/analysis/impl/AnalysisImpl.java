package com.rivernine.cryptoGeneratorBinance.schedule.analysis.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
  private BigDecimal longBlueCandleRate;
  @Value("${binance.lossCutRate}")
  private BigDecimal lossCutRate;
  @Value("${binance.marginRatePerLevel}")	
  private List<BigDecimal> marginRatePerLevel;
  @Value("${binance.scaleTradeRatePerLevel}")	
  private List<BigDecimal> scaleTradeRatePerLevel;

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
      BigDecimal minPrice = new BigDecimal(100000000.00000000);
      BigDecimal maxPrice = new BigDecimal(0.0);

      for(Candle candle: candles) {
        log.info(candle.toString());
        if(candle.getFlag() == 1) {
          log.info("getFlag == 1. Return false");
          return false;
        }
        maxPrice = maxPrice.max(candle.getOpen());
        minPrice = minPrice.min(candle.getClose());
        
        BigDecimal thresholdPrice = candle.getOpen().multiply(new BigDecimal(1).subtract(longBlueCandleRate));
        BigDecimal thresholdPrice2 = maxPrice.multiply(new BigDecimal(1).subtract(longBlueCandleRate.multiply(new BigDecimal(2))));

        log.info("threshold(two longBlueCandle) : " + thresholdPrice.toString());
        log.info("threshold(longlongBlueCandle) : " + thresholdPrice2.toString());
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

  public String calAskPrice(Integer level, Symbol symbol, String coinQuantity, BigDecimal usedBalance) {
    BigDecimal feeRate = new BigDecimal(0.0002);
    BigDecimal marginRate = marginRatePerLevel.get(level);
    BigDecimal targetBalance = usedBalance.multiply(marginRate.add(feeRate).add(new BigDecimal(1)));
    BigDecimal targetPrice = targetBalance.divide(new BigDecimal(coinQuantity), 8, RoundingMode.HALF_UP);

    targetPrice = convertTickPrice(symbol, targetPrice);
    log.info("coinQuantity : targetBalance");
    log.info(coinQuantity + " : " + targetBalance.toString());

    return targetPrice.toString();
  }

  public BigDecimal calLossCutPrice(String coinQuantity, BigDecimal usedBalance) {
    BigDecimal avgBuyPrice = usedBalance.divide(new BigDecimal(coinQuantity), 8, RoundingMode.HALF_UP);
    BigDecimal lossCutPrice = avgBuyPrice.multiply(new BigDecimal(1).subtract(lossCutRate));

    return lossCutPrice;
  }

  public Boolean judgeScaleTrade(BigDecimal curPrice, BigDecimal lastBidPrice, Integer level) {
    Boolean result;
    BigDecimal scaleTradeRate = scaleTradeRatePerLevel.get(level);
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
    log.info("price : convertedPrice");
    log.info(price.toString() + " : " + result.toString());

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
