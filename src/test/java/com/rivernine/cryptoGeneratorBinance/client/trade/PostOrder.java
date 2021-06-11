package com.rivernine.cryptoGeneratorBinance.client.trade;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;

import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.*;

public class PostOrder {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
//        System.out.println(syncRequestClient.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, TimeInForce.GTC,
//                "1", "1", null, null, null, null));

        // place dual position side order.
        // Switch between dual or both position side, call: com.rivernine.cryptoGeneratorBinance.client.trade.ChangePositionSide
        System.out.println("test");
        System.out.println(syncRequestClient.postOrder("XRPUSDT", OrderSide.BUY, PositionSide.BOTH, OrderType.TAKE_PROFIT, TimeInForce.GTC,
                "6", "0.860", null, null, "0.850", null, NewOrderRespType.RESULT));

    // postOrder(symbol, side, positionSide, orderType,
    //          timeInForce, quantity, price, reduceOnly,
    //          newClientOrderId, stopPrice, workingType, newOrderRespType)                                  ;
    }
}