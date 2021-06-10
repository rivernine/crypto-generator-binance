package com.rivernine.cryptoGeneratorBinance.schedule.user;

import com.rivernine.cryptoGeneratorBinance.schedule.user.Impl.UserImpl;
import com.rivernine.cryptoGeneratorBinance.schedule.user.dto.Balance;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserJob {
  private final UserImpl userImpl;

  public Balance getUSDTBalance() {
    return userImpl.getUSDTBalance();
  }

}
