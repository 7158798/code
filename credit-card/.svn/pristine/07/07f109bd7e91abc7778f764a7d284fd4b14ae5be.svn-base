package com.pay.card;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class test {
    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8080/credit-card/api/bank";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("deviceinfo",
                "{\"platform\":\"android/ios\",\"version\":\"version\",\"factory\":\"factory\",\"imei\":\"1234\",\"app_version\":\"2.0.1\",\"channels\":\"abc\"}");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }
    }
}
