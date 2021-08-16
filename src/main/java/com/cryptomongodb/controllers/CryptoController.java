package com.cryptomongodb.controllers;

import com.cryptomongodb.models.Crypto;
import com.cryptomongodb.services.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EnableScheduling
@RestController
public class CryptoController {

    @Autowired
    CryptoService cryptoService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void saveEvery10Seconds(){
        Crypto crypto = cryptoService.parseCurrency("BTC", "USD");
        cryptoService.save(crypto);
        Crypto crypto1 = cryptoService.parseCurrency("ETH", "USD");
        cryptoService.save(crypto1);
        Crypto crypto2 = cryptoService.parseCurrency("XRP", "USD");
        cryptoService.save(crypto2);
    }

    @GetMapping(value = "/crypto1/{s1}/{s2}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> lastPrice(@PathVariable String s1, @PathVariable String s2){
        if ((s1.equals("BTC") || s1.equals("ETH") || s1.equals("XRP")) && s2.equals("USD")){
            Crypto crypto = cryptoService.findLastByCurrencyName(s1, s2);
            return ResponseEntity.ok(crypto);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("{\"success\":false,\"error\":\"Incorrect currency name. First value can be: BTC, ETH, XRP and second value - USD\"}");
        }
    }

    @GetMapping(value = "/cryptocurrencies/minprice", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMinPrice(@RequestParam String name){
        if (name.equals("BTC") || name.equals("ETH") || name.equals("XRP")){
            return ResponseEntity.ok(cryptoService.findMinByCurrencyName(name));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"success\":false,\"error\":\"Incorrect currency name. Can be: BTC, ETH, XRP\"}");
    }

    @GetMapping(value = "/cryptocurrencies/maxprice", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMaxPrice(@RequestParam String name){
        if (name.equals("BTC") || name.equals("ETH") || name.equals("XRP")){
            return ResponseEntity.ok(cryptoService.findMaxByCurrencyName(name));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"success\":false,\"error\":\"Incorrect currency name. Can be: BTC, ETH, XRP\"}");
    }

    @GetMapping("/cryptocurrencies")
    public List<Crypto> getPrices(@RequestParam HashMap<String, String> params){
        String name = params.get("name");
        int size;
        int page;
        if (params.containsKey("size")){
            size = Integer.parseInt(params.get("size"));
        } else {
            size = 10;
        }
        if (params.containsKey("page")){
            page = Integer.parseInt(params.get("page"));
        } else {
            page = 0;
        }
        return cryptoService.findAll(name, page, size);
    }

    @GetMapping("/cryptocurrencies/csv")
    public void createCSV(){
        List<Crypto> cryptos = new ArrayList<>();
        cryptos.add(cryptoService.findMaxByCurrencyName("BTC"));
        cryptos.add(cryptoService.findMinByCurrencyName("BTC"));
        cryptos.add(cryptoService.findMaxByCurrencyName("ETH"));
        cryptos.add(cryptoService.findMinByCurrencyName("ETH"));
        cryptos.add(cryptoService.findMaxByCurrencyName("XRP"));
        cryptos.add(cryptoService.findMinByCurrencyName("XRP"));
        cryptoService.createCSV(cryptos);
    }
}
