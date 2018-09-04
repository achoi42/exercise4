package com.solstice.repository;

import com.solstice.domain.Stock;
import static org.junit.Assert.*;

import com.solstice.util.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class StockRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private StockRepository stockRepository;


  @Test
  public void testFindBySymbolNameHappyPath() {
    Stock stock = new Stock("TEST", 99);
    entityManager.persist(stock);
    Stock foundStock = stockRepository.findByName("TEST");
    assertEquals("TEST", foundStock.getName());
    assertEquals(99, foundStock.getSymbolId());
  }

  @Test
  public void testFindBySymbolNameBadPath() {
    Stock stock = new Stock("TEST", 99);
    entityManager.persist(stock);
    Stock outcome = stockRepository.findByName("TESTT");
    assertNull(outcome);
  }
}
