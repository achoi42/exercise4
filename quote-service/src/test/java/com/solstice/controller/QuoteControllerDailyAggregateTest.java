package com.solstice.controller;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.solstice.domain.Aggregate;
import com.solstice.domain.QuoteRecord;
import com.solstice.repository.QuoteRepository;
import com.solstice.util.BadRequestException;
import com.solstice.util.DateHelper;
import com.solstice.service.QuoteService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(QuoteController.class)
public class QuoteControllerDailyAggregateTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuoteRepository quoteRepository;

  @MockBean
  private DateHelper dateHelper;

  @MockBean
  private QuoteService quoteService;

  @InjectMocks
  private QuoteController quoteController;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDailyAggregateHappyPath() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);

    QuoteRecord max = new QuoteRecord(
        1, 200.0, 1000, sdf.parse("01-21-2001 13:30:58"
    ));
    Aggregate maxAg = new Aggregate("highest price", Arrays.asList(max));

    QuoteRecord min = new QuoteRecord(
        1, 100.0, 1000, sdf.parse("01-21-2001 14:30:58"
    ));
    Aggregate minAg = new Aggregate("lowest price", Arrays.asList(min));

    QuoteRecord total = new QuoteRecord(
        0, 0.0, 3000, sdf.parse("01-21-2001 12:00:00"
    ));
    Aggregate totalAg = new Aggregate("total volume", Arrays.asList(total));

    QuoteRecord closing = new QuoteRecord(
        1, 150.0, 900, sdf.parse("01-21-2001 17:00:00"
    ));
    Aggregate closingAg = new Aggregate("closing price", Arrays.asList(closing));

    when(quoteService.dailyAggregate(anyString(), anyString()))
        .thenReturn(Arrays.asList(maxAg, minAg, totalAg, closingAg));
    mockMvc
        .perform(get("/AAPL/2018-01-01"))
        .andExpect(jsonPath("$[0].title", is(equalTo("highest price"))))
        .andExpect(jsonPath("$[1].title", is(equalTo("lowest price"))))
        .andExpect(jsonPath("$[2].title", is(equalTo("total volume"))))
        .andExpect(jsonPath("$[3].title", is(equalTo("closing price"))))
        .andExpect(jsonPath("$[0].quotes[0].symbol", is(equalTo((int) max.getSymbol()))))
        .andExpect(jsonPath("$[1].quotes[0].symbol", is(equalTo((int) min.getSymbol()))))
        .andExpect(jsonPath("$[2].quotes[0].symbol", is(equalTo((int) total.getSymbol()))))
        .andExpect(jsonPath("$[3].quotes[0].symbol", is(equalTo((int) closing.getSymbol()))))
        .andExpect(jsonPath("$[0].quotes[0].price", is(anyOf(
            equalTo(max.getPrice()),
            equalTo((int) max.getPrice())
        ))))
        .andExpect(jsonPath("$[1].quotes[0].price", is(anyOf(
            equalTo(min.getPrice()),
            equalTo((int) min.getPrice())
        ))))
        .andExpect(jsonPath("$[2].quotes[0].price", is(anyOf(
            equalTo(total.getPrice()),
            equalTo((int) total.getPrice())
        ))))
        .andExpect(jsonPath("$[3].quotes[0].price", is(anyOf(
            equalTo(closing.getPrice()),
            equalTo((int) closing.getPrice())
        ))))
        .andExpect(jsonPath("$[0].quotes[0].volume", is(equalTo(max.getVolume()))))
        .andExpect(jsonPath("$[1].quotes[0].volume", is(equalTo(min.getVolume()))))
        .andExpect(jsonPath("$[2].quotes[0].volume", is(equalTo(total.getVolume()))))
        .andExpect(jsonPath("$[3].quotes[0].volume", is(equalTo(closing.getVolume()))))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void testDailyAggregateSymbolNotFound() throws Exception {
    when(quoteService.dailyAggregate(anyString(), anyString()))
        .thenThrow(new BadRequestException());

    mockMvc
        .perform(get("/BAD/2018-06-21"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void testDailyAggregateNoData() throws Exception {
    Aggregate max = new Aggregate("highest price", Collections.emptyList());
    Aggregate min = new Aggregate("lowest price", Collections.emptyList());
    Aggregate total = new Aggregate("total volume", Collections.emptyList());
    Aggregate closing = new Aggregate("closing price", Collections.emptyList());

    when(quoteService.dailyAggregate(anyString(), anyString()))
        .thenReturn(Arrays.asList(max, min, total, closing));

    mockMvc
        .perform(get("/AAPL/1234-99-99"))
        .andExpect(jsonPath("$[0].title", is(equalTo("highest price"))))
        .andExpect(jsonPath("$[1].title", is(equalTo("lowest price"))))
        .andExpect(jsonPath("$[2].title", is(equalTo("total volume"))))
        .andExpect(jsonPath("$[3].title", is(equalTo("closing price"))))
        .andExpect(jsonPath("$[0].quotes", is(empty())))
        .andExpect(jsonPath("$[1].quotes", is(empty())))
        .andExpect(jsonPath("$[2].quotes", is(empty())))
        .andExpect(jsonPath("$[3].quotes", is(empty())))
        .andExpect(status().isOk());
  }
}
