package com.solstice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;

@Service
public class DateHelper {

  public static Date stringTimestampToDate(String timestamp) {
    Date date = null;
    if(timestamp == "") {
      return date;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss.SSSX", Locale.US);
      date = sdf.parse(timestamp);
    } catch(ParseException e) {
      System.err.println("Error parsing timestamp from load endpoint");
      e.printStackTrace();
    }
    return date;
  }

  public Date stringToDate(String date) {
    Date myDate = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
      myDate = sdf.parse(date);
    } catch(ParseException e) {
      System.err.println("Error parsing date from get request");
      e.printStackTrace();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(myDate);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  public Date getEnd(Date date) {
    if (date == null) {
      return null;
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 999);
    return c.getTime();
  }

  public Date stringToMonthStart(String month) {
    Date myMonth = null;
    try {
      myMonth = new SimpleDateFormat("yyyy-MM").parse(month);
    } catch(ParseException e) {
      System.err.println("Error parsing month from get request");
      e.printStackTrace();
    }
    Calendar c = Calendar.getInstance();
    c.setTime(myMonth);
    c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  public Date getEndOfMonth(Date monthStart) {
    if(monthStart == null) {
      return null;
    }
    Calendar c = Calendar.getInstance();
    c.setTime(monthStart);
    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
    return getEnd(c.getTime());
  }
}