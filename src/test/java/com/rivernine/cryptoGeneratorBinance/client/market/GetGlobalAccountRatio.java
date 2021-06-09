package com.rivernine.cryptoGeneratorBinance.client.market;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.PeriodType;

public class GetGlobalAccountRatio {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        System.out.println(syncRequestClient.getGlobalAccountRatio("BTCUSDT", PeriodType._5m,null,null,10));


    }
}
