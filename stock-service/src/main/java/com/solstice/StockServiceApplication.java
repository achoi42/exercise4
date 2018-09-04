package com.solstice;

import static java.util.Arrays.asList;

import com.solstice.domain.Stock;
import com.solstice.repository.StockRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class StockServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StockServiceApplication.class, args);
  }

  @Bean
  CommandLineRunner addStocks(StockRepository stockRepository) {
    return args -> {
      List<Stock> stocks = asList(
          new Stock("AAPL", 1),
          new Stock("GOOG", 2),
          new Stock("MSFT", 3),
          new Stock("PVTL", 4),
          new Stock("AMZN", 5)
      );
      stockRepository.deleteAll();
      stockRepository.save(stocks);
    };
  }
}
