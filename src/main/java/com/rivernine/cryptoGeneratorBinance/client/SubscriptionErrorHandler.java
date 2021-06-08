package com.rivernine.cryptoGeneratorBinance.client;

import com.rivernine.cryptoGeneratorBinance.client.exception.BinanceApiException;

/**
 * The error handler for the subscription.
 */
@FunctionalInterface
public interface SubscriptionErrorHandler {

  void onError(BinanceApiException exception);
}
