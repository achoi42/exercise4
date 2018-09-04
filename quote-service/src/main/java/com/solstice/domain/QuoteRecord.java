package com.solstice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solstice.util.DateHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class QuoteRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long symbol;
  private double price;
  private int volume = -1;

  @JsonFormat(shape = Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date date;

  protected QuoteRecord() {
  }

  public QuoteRecord(long symbol, double price, int volume, String date) {
    this.symbol = symbol;
    this.price = price;
    this.volume = volume;
    this.date = DateHelper.stringTimestampToDate(date);
  }

  public QuoteRecord(long symbol, double price, int volume, Date date) {
    this.symbol = symbol;
    this.price = price;
    this.volume = volume;
    this.date = date;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSymbol() {
    return symbol;
  }

  public void setSymbol(long symbol) {
    this.symbol = symbol;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public Date getDate() {
    return date;
  }

  @JsonIgnore
  public String getStringDate() {
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC-5"));
    if(date == null) {
      return "";
    }
    String strDate = dateFormat.format(date);
    return strDate;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public void setDate(String date) {
    this.date = DateHelper.stringTimestampToDate(date);
  }
}
