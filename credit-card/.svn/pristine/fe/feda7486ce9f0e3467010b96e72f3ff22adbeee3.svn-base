package com.pay.card.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.CreditCardApplication;

@RunWith(SpringJUnit4ClassRunner.class)
// @RunWith(SpringRunner.class)
@SpringBootTest(classes = CreditCardApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class SpringbootHttpRequestTest {

    private TestRestTemplate template = new TestRestTemplate();

    @SuppressWarnings("rawtypes")
    @Test
    public void testFindCreditSetList() {
        //
        String url = "http://127.0.0.1:8081/credit-card-client/api/v1/data/querySet";
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("username", "lake");
        multiValueMap.add("auth_token", "qwertyui");

        JSONObject parm = new JSONObject();
        parm.put("parm", "1234");
        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(parm, multiValueMap);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, entity, String.class);
        String body = response.getBody();
        MediaType contentType = response.getHeaders().getContentType();
        HttpStatus statusCode = response.getStatusCode();

        System.out.println("response body:" + body);
        System.out.println("response contentType:" + contentType);
        System.out.println("response statusCode:" + statusCode);
    }
}
