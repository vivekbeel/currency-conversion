package com.learn.microservice.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.learn.microservice.currencyconversionservice.bean.CurrencyConversionDao;
import com.learn.microservice.currencyconversionservice.util.CurrencyExchangeProxy;

@RestController
public class CurrencyConversionController {

	@Autowired
	CurrencyExchangeProxy currencyExchangeProxy;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionDao calculateCurrencyConversion(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		HashMap<String, String> uriVariable=new HashMap<>();
		uriVariable.put("from", from);
		uriVariable.put("to", to);
		ResponseEntity<CurrencyConversionDao> responseEntity= new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionDao.class, uriVariable);
		CurrencyConversionDao currencyConversionDao= responseEntity.getBody();
		return new CurrencyConversionDao(1001, from, to, quantity, currencyConversionDao.getConversionMultiple(),
				quantity.multiply(currencyConversionDao.getConversionMultiple()), currencyConversionDao.getEnvironment());
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionDao calculateCurrencyConversionFeign(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		
		CurrencyConversionDao currencyConversionDao= currencyExchangeProxy.retriveExchangeValue(from, to);
		return new CurrencyConversionDao(1001, from, to, quantity, currencyConversionDao.getConversionMultiple(),
				quantity.multiply(currencyConversionDao.getConversionMultiple()), currencyConversionDao.getEnvironment());
	}
}
