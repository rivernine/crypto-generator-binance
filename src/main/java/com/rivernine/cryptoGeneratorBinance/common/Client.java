package com.rivernine.cryptoGeneratorBinance.common;

import com.rivernine.cryptoGeneratorBinance.client.SyncRequestClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Component
@NoArgsConstructor
@Getter
@Setter
public class Client {

  @Value("${binance.apiKey}")
  public String apiKey;
  @Value("${binance.secretKey}")
  public String secretKey;
  
  public SyncRequestClient queryClient;
  public SyncRequestClient invokeClient;

  public void init() {
    this.queryClient = SyncRequestClient.create();
    this.invokeClient = SyncRequestClient.create(this.apiKey, this.secretKey);
  }
}
