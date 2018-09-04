package com.solstice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  @Column(unique = true)
  private long symbolId;

  protected Stock() {
  }

  public Stock(String name, long symbolId) {
    this.name = name;
    this.symbolId = symbolId;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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
