package com.rivernine.cryptoGeneratorBinance.client.impl;

import com.rivernine.cryptoGeneratorBinance.client.impl.utils.JsonWrapper;

@FunctionalInterface
public interface RestApiJsonParser<T> {

  T parseJson(JsonWrapper json);
}
