package com.eduardo.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Name is important: Exact same name as the one we wrote in the file application.propeties of the other microservice
@FeignClient(name = "netflix-zuul-api-gateway-server")		// Calling the Zuul server name
@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchangeServiceFeignProxy {

	@GetMapping("/currency-exchange-service/currency-exchange/from/{currencyFrom}/to/{currencyTo}")		// Note the name of the service we want to ultimately call, added at the beginning of this path
	public CurrencyConversionBean retrieveCurrencyExchangeValue(
			@PathVariable String currencyFrom, @PathVariable String currencyTo);
}