package com.rivernine.cryptoGeneratorBinance.client.websocket;

import com.rivernine.cryptoGeneratorBinance.client.SubscriptionClient;

public class SubscribeSymbolBookTicker {

    public static void main(String[] args) {

        SubscriptionClient client = SubscriptionClient.create();
   
        client.subscribeSymbolBookTickerEvent("btcusdt", ((event) -> {
            System.out.println(event);
            client.unsubscribeAll();
        }), null);

    }

}
