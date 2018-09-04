package com.solstice.controller;

import static org.junit.Assert.*;
import com.solstice.domain.QuoteRecord;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class QuoteControllerLoadIntegrationTest {

  @Autowired
  QuoteController quoteController;

  @Test
  public void testLoadHappyPath() throws IOException {
    List<QuoteRecord> dataset = quoteController.load();
    assertNotNull(dataset);
    assertEquals(dataset.size(), 60);
  }
}
