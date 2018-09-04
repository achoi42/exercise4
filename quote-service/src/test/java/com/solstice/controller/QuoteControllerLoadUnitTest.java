package com.solstice.controller;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.solstice.domain.QuoteRecord;
import com.solstice.repository.QuoteRepository;
import com.solstice.util.BadRequestException;
import com.solstice.util.DateHelper;
import com.solstice.service.QuoteService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
public class QuoteControllerLoadUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QuoteRepository quoteRepository;

  @MockBean
  private QuoteService quoteService;

  @MockBean
  private DateHelper dateHelper;

  @InjectMocks
  private QuoteController quoteController;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testLoadHappyPath() throws Exception{
    Date myDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US).parse("01-21-2001 13:30:58");
    QuoteRecord quote = new QuoteRecord(99, 0.0, 0, myDate);

    when(quoteService.loadJson())
        .thenReturn(Arrays.asList(quote));

    mockMvc
        .perform(post("/load"))
        .andExpect(jsonPath("$[0].symbol", is(anyOf(
            equalTo((int) quote.getSymbol()),
            equalTo(quote.getSymbol())
            ))))
        .andExpect(jsonPath("$[0].price", is(equalTo(quote.getPrice()))))
        .andExpect(jsonPath("$[0].volume", is(equalTo(quote.getVolume()))))
        .andExpect(jsonPath("$[0].date", is(equalTo(quote.getStringDate()))))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  public void testLoadBadRequest() throws Exception{
    when(quoteService.loadJson())
        .thenThrow(new BadRequestException());
    mockMvc
        .perform(post("/load"))
        .andExpect(status().is4xxClientError())
        .andReturn();
  }
}
