package com.cryptomongodb.services;

import com.cryptomongodb.dao.CryptoDAO;
import com.cryptomongodb.models.Crypto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoService {

    @Autowired
    CryptoDAO cryptoDao;

    public void save(Crypto crypto) {
        cryptoDao.save(crypto);
    }

    public Crypto findLastByCurrencyName(String curr1, String curr2) {
        return cryptoDao.findFirstByCurr1AndCurr2OrderByIDDesc(curr1, curr2);
    }

    public Crypto findMinByCurrencyName(String name) {
        return cryptoDao.findTopByCurr1OrderByLpriceAsc(name);
    }

    public Crypto findMaxByCurrencyName(String name) {
        return cryptoDao.findTopByCurr1OrderByLpriceDesc(name);
    }

    public Crypto parseCurrency(String s1, String s2) {
        StringBuilder response = new StringBuilder();
        try {
            String url_str = "https://cex.io/api/last_price/" + s1 + "/" + s2;
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.addRequestProperty("User-Agent", "Chrome");
            request.setRequestMethod("GET");
            InputStream inputStream = request.getInputStream();

            int read;
            while ((read = inputStream.read()) != -1) {
                response.append((char) read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Crypto crypto = new Crypto();
        try {
            JSONArray jsonArray = new JSONArray("[" + response + "]");
            JSONObject dateFromNet = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++) {
                dateFromNet = (JSONObject) jsonArray.get(i);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
            crypto = new Crypto(
                    dateFromNet.getDouble("lprice"),
                    dateFromNet.getString("curr1"),
                    dateFromNet.getString("curr2"),
                    currentTime
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return crypto;
    }

    public List<Crypto> findAll(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lprice"));
        return cryptoDao.findAllByCurr1(name, pageable);
    }

    public void createCSV(List<Crypto> cryptos) {
        String[] nameColumns = {"Currency name", "minPrice", "maxPrice"};
        String[] BTC = {cryptos.get(1).getCurr1(), String.valueOf(cryptos.get(1).getLprice()), String.valueOf(cryptos.get(0).getLprice())};
        String[] ETH = {cryptos.get(3).getCurr1(), String.valueOf(cryptos.get(3).getLprice()), String.valueOf(cryptos.get(2).getLprice())};
        String[] XPR = {cryptos.get(5).getCurr1(), String.valueOf(cryptos.get(5).getLprice()), String.valueOf(cryptos.get(4).getLprice())};
        List<String[]> list = new ArrayList<>();
        list.add(nameColumns);
        list.add(BTC);
        list.add(ETH);
        list.add(XPR);
        File csvOutputFile = new File("Currency.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            list.stream().map(date -> String.join("\t", date)).forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
