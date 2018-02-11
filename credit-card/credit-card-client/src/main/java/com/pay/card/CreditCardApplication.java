package com.pay.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class CreditCardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardApplication.class, args);
    }
}
