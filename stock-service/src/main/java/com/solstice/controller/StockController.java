package com.solstice.controller;

import com.solstice.domain.Stock;
import com.solstice.repository.StockRepository;
import com.solstice.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

  private StockRepository stockRepository;

  protected StockController() {
  }

  @Autowired
  public StockController(StockRepository stockRepository) {
    this.stockRepository = stockRepository;
  }

  @GetMapping("/{name}")
  public Stock get(@PathVariable(name = "name") String name) {
     Stock response = stockRepository.findByName(name);
     if(response == null) {
       System.err.println("NULL RESPONSE GETTING SYMBOL ID");
       throw new NotFoundException();
     }
     return response;
  }
}
