package com.rivernine.cryptoGeneratorBinance.schedule.account;

import com.rivernine.cryptoGeneratorBinance.schedule.account.Impl.AccountImpl;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AccountJob {
  private final AccountImpl accountImpl;

  public void getAccount() {
    accountImpl.getAccount();
  }

}
