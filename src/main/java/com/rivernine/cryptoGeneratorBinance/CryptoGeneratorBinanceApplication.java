package com.rivernine.cryptoGeneratorBinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CryptoGeneratorBinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoGeneratorBinanceApplication.class, args);
	}

}
