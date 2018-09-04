package com.solstice.repository;

import com.solstice.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

  @Query("SELECT s FROM Stock s WHERE s.name = :name")
  Stock findByName(@Param("name") String name);
}
