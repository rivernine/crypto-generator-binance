package com.rivernine.cryptoGeneratorBinance.client.websocket;

import com.rivernine.cryptoGeneratorBinance.client.SubscriptionClient;

public class SubscribeAllBookTicker {

    public static void main(String[] args) {

        SubscriptionClient client = SubscriptionClient.create();
   
        client.subscribeAllBookTickerEvent(System.out::println, null);

    }

}
