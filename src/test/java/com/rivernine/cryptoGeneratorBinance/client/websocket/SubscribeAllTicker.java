package com.rivernine.cryptoGeneratorBinance.client.websocket;

import com.rivernine.cryptoGeneratorBinance.client.SubscriptionClient;

public class SubscribeAllTicker {

    public static void main(String[] args) {

        SubscriptionClient client = SubscriptionClient.create();
   
        client.subscribeAllTickerEvent(((event) -> {
            System.out.println(event);
            client.unsubscribeAll();
        }), null);

    }

}
