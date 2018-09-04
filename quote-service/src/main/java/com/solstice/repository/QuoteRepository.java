package com.solstice.repository;

import com.solstice.domain.QuoteRecord;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<QuoteRecord, Long> {
  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym"
      + " AND q.date BETWEEN :date AND :endOfDate"
      + " AND q.price ="
      + "(SELECT MAX(maxq.price) FROM QuoteRecord maxq WHERE maxq.symbol = :sym"
      + " AND maxq.date BETWEEN :date AND :endOfDate)")
  List<QuoteRecord> findDailyMax(@Param("sym") long sym, @Param("date") Date date, @Param("endOfDate") Date endOfDate);

  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym"
      + " AND q.date BETWEEN :date AND :endOfDate"
      + " AND q.price = " +
      "(SELECT MIN(minq.price) FROM QuoteRecord minq " +
      "WHERE minq.symbol = :sym AND minq.date BETWEEN :date AND :endOfDate)")
  List<QuoteRecord> findDailyMin(@Param("sym") long sym, @Param("date") Date date, @Param("endOfDate") Date endOfDate);

  @Query("SELECT SUM(q.volume) FROM QuoteRecord q " +
      "WHERE q.symbol = :sym AND q.date BETWEEN :date AND :endOfDate")
  Integer findDailyVolume(@Param("sym") long sym, @Param("date") Date date, @Param("endOfDate") Date endOfDate);

  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym AND q.date = " +
      "(SELECT MAX(closeq.date) FROM QuoteRecord closeq " +
      "WHERE closeq.symbol = :sym AND closeq.date BETWEEN :date AND :endOfDate)")
  List<QuoteRecord> findClosingPrice(@Param("sym") long sym, @Param("date") Date date, @Param("endOfDate") Date endOfDate);

  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym AND q.price =" +
      "(SELECT MAX(maxq.price) FROM QuoteRecord maxq " +
      "WHERE maxq.symbol = :sym AND maxq.date BETWEEN :monthStart AND :monthEnd)")
  List<QuoteRecord> findMonthlyMax(@Param("sym") long sym, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd);

  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym AND q.price =" +
      "(SELECT MIN(maxq.price) FROM QuoteRecord maxq " +
      "WHERE maxq.symbol = :sym AND maxq.date BETWEEN :monthStart AND :monthEnd)")
  List<QuoteRecord> findMonthlyMin(@Param("sym") long sym, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd);

  @Query("SELECT SUM(q.volume) FROM QuoteRecord q " +
      "WHERE q.symbol = :sym AND q.date BETWEEN :monthStart AND :monthEnd")
  Integer findMonthlyVolume(@Param("sym") long sym, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd);

  @Query("SELECT q FROM QuoteRecord q WHERE q.symbol = :sym"
      + " AND q.date = :date")
  QuoteRecord findByDateAndAndSymbol(@Param("sym") long sym, @Param("date") Date date);
}
