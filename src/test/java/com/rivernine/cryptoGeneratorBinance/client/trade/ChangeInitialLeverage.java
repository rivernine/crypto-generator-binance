package com.rivernine.cryptoGeneratorBinance.client.trade;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;

import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;

public class ChangeInitialLeverage {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY);
        System.out.println(syncRequestClient.changeInitialLeverage("XRPUSDT", 1));
        
    }
}