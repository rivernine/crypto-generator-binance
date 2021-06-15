package com.rivernine.cryptoGeneratorBinance.client.trade;

import java.util.List;

import com.rivernine.cryptoGeneratorBinance.client.RequestOptions;
import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;
import com.rivernine.cryptoGeneratorBinance.client.constants.PrivateConfig;
import com.rivernine.cryptoGeneratorBinance.client.model.trade.PositionRisk;

public class GetPositionRisk {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        List<PositionRisk> res = syncRequestClient.getPositionRisk();
        for(PositionRisk position: res) {
          if(position.getSymbol().equals("ADAUSDT")) {
            System.out.println(position.toString());
          }
        }
    }
}

// PositionRisk[
//     entryPrice=1.5849,
//     leverage=1,
//     maxNotionalValue=9.223372036854776E18,
//     liquidationPrice=0.00063811,
//     markPrice=1.58487837,
//     positionAmt=4,
//     symbol=ADAUSDT,
//     unrealizedProfit=-0.00008652,
//     isolatedMargin=6.33697764,
//     positionSide=BOTH,
//     marginType=isolated
// ]

// PositionRisk[
//     entryPrice=1.5836,
//     leverage=2,
//     maxNotionalValue=9.223372036854776E18,
//     liquidationPrice=0.39864138
//     markPrice=1.58061898,
//     positionAmt=8,
//     symbol=ADAUSDT,
//     unrealizedProfit=-0.02384816,
//     isolatedMargin=9.47655016,
//     positionSide=BOTH,
//     marginType=isolated
//     ]
    