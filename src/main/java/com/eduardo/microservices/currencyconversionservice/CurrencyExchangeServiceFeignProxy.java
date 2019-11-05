package com.eduardo.microservices.currencyconversionservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Name is important: Exact same name as the one we wrote in the file application.propeties of the other microservice
@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
public interface CurrencyExchangeServiceFeignProxy {
	
	// Copy-paste the method retrieveCurrencyExchangeValue() from the original file CurrencyExchangeValueResource.java
	// The only things to change:
	// 1) The return type from the original CurrencyExchangeValue to the current CurrencyConversionBean (the other bean
	//    literally don't exist here).
	// 2) Delete the implementation, just leave the definition of the method.
	@GetMapping("/currency-exchange/from/{currencyFrom}/to/{currencyTo}")
	public CurrencyConversionBean retrieveCurrencyExchangeValue(
			@PathVariable String currencyFrom, @PathVariable String currencyTo);
}