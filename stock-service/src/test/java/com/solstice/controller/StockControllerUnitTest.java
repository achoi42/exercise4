package com.solstice.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.solstice.domain.Stock;
import com.solstice.repository.StockRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Stock.class})
@WebMvcTest(StockController.class)
public class StockControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StockRepository stockRepository;

  @InjectMocks
  private StockController stockController;

  @Rule
  public ExpectedException exceptionGrabber = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetStockHappyPath() throws Exception{
    Stock outcome = new Stock("TEST", 99);
    when(stockRepository.findByName(anyString()))
        .thenReturn(outcome);


    mockMvc
        .perform(get("/TEST"))
        .andExpect(jsonPath("$.name", is(outcome.getName())))
        .andExpect(jsonPath("$.symbolId", is(anyOf(
            equalTo((int) outcome.getSymbolId()),
            equalTo(outcome.getSymbolId())
        ))))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void testGetStockBadName() throws Exception {

    when(stockRepository.findByName(anyString()))
        .thenReturn(null);

    mockMvc
        .perform(get("/TESTT"))
        .andExpect(status().is4xxClientError());
  }
}
