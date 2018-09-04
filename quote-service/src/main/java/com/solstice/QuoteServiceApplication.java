package com.solstice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QuoteServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuoteServiceApplication.class, args);
  }
}
