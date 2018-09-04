package com.solstice.domain;

public class Stock {

  private String name;
  private long symbolId;

  protected Stock() {
  }

  public Stock(String name, long symbolId) {
    this.name = name;
    this.symbolId = symbolId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSymbolId() {
    return symbolId;
  }

  public void setSymbolId(long symbolId) {
    this.symbolId = symbolId;
  }
}
