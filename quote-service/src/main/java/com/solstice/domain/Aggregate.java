package com.solstice.domain;

import java.util.List;

public class Aggregate {

  private String title;
  private List<QuoteRecord> quotes;

  public Aggregate(String title, List<QuoteRecord> quotes) {
    this.title = title;
    this.quotes = quotes;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<QuoteRecord> getQuotes() {
    return quotes;
  }

  public void setQuotes(List<QuoteRecord> quotes) {
    this.quotes = quotes;
  }

  public void addQuote(QuoteRecord quote) {
    this.quotes.add(quote);
  }
}
