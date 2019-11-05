package com.eduardo.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionResource {

	@Autowired
	private CurrencyExchangeServiceFeignProxy currencyExchangeServiceFeignProxy;

	@GetMapping("/currency-converter/from/{currencyFrom}/to/{currencyTo}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(
			@PathVariable String currencyFrom, @PathVariable String currencyTo, @PathVariable BigDecimal quantity) {

		/* Call the other microservice currency-exchange to get the exchange rate (a.k.a. conversionFactor) */
		// Step 1) Create a Map<String, String> with the variable values we'll pass to the other microservice
		Map<String, String> currenciesToConvert= new HashMap<String, String>();
		currenciesToConvert.put("fromCurrency", currencyFrom);
		currenciesToConvert.put("toCurrency", currencyTo);

		// Step 2) Use the method RestTemplate().getForEntity() passing the URL of the other microservice,
		//         the expected return bean's class name, and the map from Step 1.
		//         Note the variable names in the URI below is the same as the variable names in the Map above.

		ResponseEntity<CurrencyConversionBean> theOtherMicroservicesResponse = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{fromCurrency}/to/{toCurrency}",
				CurrencyConversionBean.class, currenciesToConvert);
		// Note that both beans (the one from the other microservice CurrencyExchangeValue.java and this one's
		// CurrencyConversionBean.java) have almost the same field names and field types in common, so I assume
		// it automatically fills the values from the other bean into the new local bean, for the common fields.

		// Step 3) Take the info from the received bean using method .getBody()
		CurrencyConversionBean receivedBean = theOtherMicroservicesResponse.getBody();
		BigDecimal conversionFactor =	receivedBean.getConversionFactor();

		// Step 4) Use that info in this microservice
		//         In this case, the receivedBean was created and automatically filled up with some info from the
		//         other microservice, but some of its fields were not filled up. We'll have to fill them manually
		//         before returning it.
		receivedBean.setQuantity(quantity);
		// Multiply the quantity by the conversion factor, to get the total
		receivedBean.setTotalCalculatedAmount(quantity.multiply(conversionFactor));
		// Note: For this case, the port number remains the one from the other microservice, we don't change it.

		return receivedBean;
	}

	@GetMapping("/currency-converter-using-feign/from/{currencyFrom}/to/{currencyTo}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyUsingFeign(
			@PathVariable String currencyFrom, @PathVariable String currencyTo, @PathVariable BigDecimal quantity) {

		// With Feign, the above steps 1 and 2 are eliminated, and establishes an easier way of doing Step 3
		CurrencyConversionBean receivedBean = currencyExchangeServiceFeignProxy.retrieveCurrencyExchangeValue(currencyFrom, currencyTo);
		// The rest stays the same
		BigDecimal conversionFactor =	receivedBean.getConversionFactor();
		receivedBean.setQuantity(quantity);
		receivedBean.setTotalCalculatedAmount(quantity.multiply(conversionFactor));

		return receivedBean;
	}
}