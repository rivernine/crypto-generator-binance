package com.rivernine.cryptoGeneratorBinance.client.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.NewOrderRespType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.OrderType;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.PositionSide;
import com.rivernine.cryptoGeneratorBinance.client.model.enums.TimeInForce;

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

			// System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.BOTH, OrderType.LIMIT, 
			// 				TimeInForce.GTC, "0.003", "2480", "true", 
			// 				null,	null, null, NewOrderRespType.RESULT));

			System.out.println(syncRequestClient.postOrder("ETHUSDT", OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, 
							null, "0.003", null, "true", 
							null,	null, null, NewOrderRespType.RESULT));

			// BigDecimal totalChange = new BigDecimal(0);
			// BigDecimal open = new BigDecimal(40238);
			// BigDecimal close = new BigDecimal(40481);
			// totalChange = totalChange.add(open.divide(close, 8, RoundingMode.HALF_UP).subtract(new BigDecimal(1)));
			// System.out.println(totalChange.toString());
	// postOrder(symbol, side, positionSide, orderType,
	//          timeInForce, quantity, price, reduceOnly,
	//          newClientOrderId, stopPrice, workingType, newOrderRespType)                                  ;
	}
}