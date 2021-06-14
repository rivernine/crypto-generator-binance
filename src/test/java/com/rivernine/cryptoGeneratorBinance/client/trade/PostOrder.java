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
			// System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.BUY, PositionSide.BOTH, OrderType.LIMIT, 
			// 				TimeInForce.GTC, "0.003", "2498", null, 
			// 				null, null, null, NewOrderRespType.RESULT));

			System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, 
							TimeInForce.GTC, "0.003", "2480", "true", 
							null,	null, null, NewOrderRespType.RESULT));

	// postOrder(symbol, side, positionSide, orderType,
	//          timeInForce, quantity, price, reduceOnly,
	//          newClientOrderId, stopPrice, workingType, newOrderRespType)                                  ;
	}
}