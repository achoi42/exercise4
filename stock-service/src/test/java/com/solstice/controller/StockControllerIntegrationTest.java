package com.solstice.controller;

import com.solstice.domain.Stock;
import static org.junit.Assert.*;

import com.solstice.util.NotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StockControllerIntegrationTest {

  @Autowired
  StockController stockController;

  @Rule
  public ExpectedException exceptionGrabber = ExpectedException.none();

  @Test
  public void testGetStockHappyPath() {
    Stock outcome = stockController.get("AAPL");

    assertNotNull(outcome);
    assertEquals("AAPL", outcome.getName());
    assertEquals(1, outcome.getSymbolId());
  }

  @Test
  public void testGetStockBadName() {
    exceptionGrabber.expect(NotFoundException.class);
    Stock outcome = stockController.get("DEADBEEF");

  }
}
