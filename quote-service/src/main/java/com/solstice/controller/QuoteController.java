package com.solstice.controller;

import com.solstice.domain.Aggregate;
import com.solstice.domain.QuoteRecord;
import com.solstice.service.QuoteService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

  private QuoteService quoteService;

  protected QuoteController() {
  }

  @Autowired
  public QuoteController(QuoteService quoteService) {
    this.quoteService = quoteService;
  }

  @DeleteMapping("/delete")
  public void delete() {
    quoteService.clearTable();
  }

  @PostMapping("/load")
  public List<QuoteRecord> load() throws IOException {
    return quoteService.loadJson();
  }

  @GetMapping("/list")
  public List<QuoteRecord>list() {
    return quoteService.listQuotes();
  }

  @GetMapping("/{sym}/{date}")
  public List<Aggregate> daily(@PathVariable(name="sym") String sym, @PathVariable(name = "date") String date) {
    return quoteService.dailyAggregate(sym, date);
  }

  @GetMapping("/monthly/{sym}/{month}")
  public List<Aggregate> monthly(@PathVariable(name="sym") String sym, @PathVariable(name = "month") String month) {
    return quoteService.monthlyAggregate(sym, month);
  }
}
