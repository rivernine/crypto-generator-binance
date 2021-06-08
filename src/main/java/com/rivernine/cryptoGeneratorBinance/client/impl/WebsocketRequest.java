package com.rivernine.cryptoGeneratorBinance.client.impl;

import com.rivernine.cryptoGeneratorBinance.client.SubscriptionErrorHandler;
import com.rivernine.cryptoGeneratorBinance.client.SubscriptionListener;
import com.rivernine.cryptoGeneratorBinance.client.impl.utils.Handler;

class WebsocketRequest<T> {

    WebsocketRequest(SubscriptionListener<T> listener, SubscriptionErrorHandler errorHandler) {
        this.updateCallback = listener;
        this.errorHandler = errorHandler;
    }

    String signatureVersion = "2";
    String name;
    Handler<WebSocketConnection> connectionHandler;
    Handler<WebSocketConnection> authHandler = null;
    final SubscriptionListener<T> updateCallback;
    RestApiJsonParser<T> jsonParser;
    final SubscriptionErrorHandler errorHandler;
}
