package com.rivernine.cryptoGeneratorBinance.client.market;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;

import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;

public class GetLiquidationOrders {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        System.out.println(syncRequestClient.getLiquidationOrders("BTCUSDT", null, null, null));
    }
}