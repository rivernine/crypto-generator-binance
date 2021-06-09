package com.rivernine.cryptoGeneratorBinance.client.trade;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;

/**
 * @author : wangwanlu
 * @since : 2020/3/26, Thu
 **/
public class BatchPlaceOrders {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);

        // place dual position side orders.
        // Switch between dual or both position side, call: com.rivernine.cryptoGeneratorBinance.client.trade.ChangePositionSide
        System.out.println(syncRequestClient.postBatchOrders(
                "[{\"symbol\": \"BTCUSDT\",\"side\":\"BUY\",\"positionSide\":\"LONG\",\"type\":\"LIMIT\",\"newClientOrderId\":\"wanlu_dev_0324\",\"quantity\":\"1\",\"price\": \"8000\",\"timeInForce\":\"GTC\"},\n" +
                "{\"symbol\": \"BTCUSDT\",\"side\":\"BUY\",\"positionSide\":\"SHORT\",\"type\":\"LIMIT\",\"newClientOrderId\":\"wanlu_dev_0325\",\"quantity\":\"1\",\"price\": \"8000\",\"timeInForce\":\"GTC\"},\n" +
                "{\"symbol\": \"BTCUSDT\",\"side\":\"BUY\",\"type\":\"LIMIT\",\"newClientOrderId\":\"wanlu_dev_0320\",\"quantity\":\"1\",\"price\": \"8000\",\"timeInForce\":\"GTC\"}]"

        ));
    }
}
