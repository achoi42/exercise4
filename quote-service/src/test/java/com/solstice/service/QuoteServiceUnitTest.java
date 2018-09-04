package com.solstice.service;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.solstice.domain.Aggregate;
import com.solstice.domain.QuoteRecord;
import com.solstice.repository.QuoteRepository;
import com.solstice.util.BadRequestException;
import com.solstice.util.DateHelper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class QuoteServiceUnitTest {

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private DateHelper dateHelper;

  @InjectMocks
  private QuoteService quoteService;

  @Rule
  public ExpectedException exceptionGrabber = ExpectedException.none();

  @Spy
  private QuoteService spyQuoteService;

  private QuoteRecord max;
  private QuoteRecord min;
  private QuoteRecord total;
  private QuoteRecord closing;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);

    max = new QuoteRecord(1, 200.0, 1000, sdf.parse("01-21-2001 13:30:58"));
    min = new QuoteRecord(1, 100.0, 1000, sdf.parse("01-21-2001 14:30:58"));
    total = new QuoteRecord(0, 0.0, 3000, sdf.parse("01-21-2001 12:00:00"));
    closing = new QuoteRecord(1, 150.0, 900, sdf.parse("01-21-2001 17:00:00"));
  }

  @Test
  public void testLoadJsonHappyPath() throws IOException {
    ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
    doReturn(Collections.emptyList()).when(mockObjectMapper).readValue(any(URL.class), Mockito.<TypeReference<List<QuoteRecord>>>any());

    when(quoteRepository.save(anyList()))
        .thenReturn(Arrays.asList(max, min, total, closing));

    exceptionGrabber.expect(MalformedURLException.class);

    List<QuoteRecord> outcome = quoteService.loadJson();

    assertThat(outcome.size(), is(equalTo(4)));
    assertNotNull(outcome.get(0));
    assertNotNull(outcome.get(1));
    assertNotNull(outcome.get(2));
    assertNotNull(outcome.get(3));
    assertTrue(outcome.contains(max));
    assertTrue(outcome.contains(min));
    assertTrue(outcome.contains(total));
    assertTrue(outcome.contains(closing));
  }

  @Test
  public void testLoadJsonBadPath() throws IOException{
    exceptionGrabber.expect(MalformedURLException.class);
    List<QuoteRecord> outcome = quoteService.loadJson();
    assertThat(outcome, is(equalTo(null)));
  }

  @Test
  public void testLoadJsonNoData() throws IOException {
    exceptionGrabber.expect(MalformedURLException.class);
    ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
    doReturn(Collections.emptyList()).when(mockObjectMapper).readValue(any(URL.class), Mockito.<TypeReference<List<QuoteRecord>>>any());
    List<QuoteRecord> outcome = quoteService.loadJson();
    assertThat(outcome,is(empty()));
  }

  @Test
  public void testDailyAggregateHappyPath() throws Exception{
    spyQuoteService = spy(quoteService);
    // doReturn stubs method call for QuoteService helper method symbolToId(String symbol)
    doReturn((long) 1).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findDailyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Arrays.asList(max));
    when(quoteRepository.findDailyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Arrays.asList(min));
    when(quoteRepository.findDailyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(5900);
    when(quoteRepository.findClosingPrice(anyLong(), anyObject(), anyObject()))
        .thenReturn(Arrays.asList(closing));

    List<Aggregate> outcome = spyQuoteService.dailyAggregate("TEST", "2001-03-01");

    assertThat(outcome.get(0).getTitle(), is(equalTo("highest price")));
    assertThat(outcome.get(1).getTitle(), is(equalTo("lowest price")));
    assertThat(outcome.get(2).getTitle(), is(equalTo("total volume")));
    assertThat(outcome.get(3).getTitle(), is(equalTo("closing price")));
    assertThat(outcome.get(0).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(1).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(2).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(3).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(0).getQuotes().get(0).getPrice(), is(equalTo( 200.0)));
    assertThat(outcome.get(1).getQuotes().get(0).getPrice(), is(equalTo( 100.0)));
    assertThat(outcome.get(2).getQuotes().get(0).getPrice(), is(equalTo( 0.0)));
    assertThat(outcome.get(3).getQuotes().get(0).getPrice(), is(equalTo( 150.0)));
    assertThat(outcome.get(0).getQuotes().get(0).getVolume(), is(equalTo( 1000)));
    assertThat(outcome.get(1).getQuotes().get(0).getVolume(), is(equalTo( 1000)));
    assertThat(outcome.get(2).getQuotes().get(0).getVolume(), is(equalTo( 5900)));
    assertThat(outcome.get(3).getQuotes().get(0).getVolume(), is(equalTo( 900)));
  }

  @Test
  public void testDailyAggregateBadPath() {
    spyQuoteService = spy(quoteService);
    // doThrow stubs method call for QuoteService helper method symbolToId(String symbol)
    doThrow(new BadRequestException()).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findDailyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findDailyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findDailyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(-1);
    when(quoteRepository.findClosingPrice(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());

    exceptionGrabber.expect(BadRequestException.class);
    spyQuoteService.dailyAggregate("BAD", "2001-03-01");
  }

  @Test
  public void testDailyAggregateNoData() {
    spyQuoteService = spy(quoteService);
    // doReturn stubs method call for QuoteService helper method symbolToId(String symbol)
    doReturn((long) -1).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findDailyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findDailyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findDailyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(-1);
    when(quoteRepository.findClosingPrice(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());

    List<Aggregate> outcome = spyQuoteService.dailyAggregate("AAPL","2020-02-20");
    assertThat(outcome.get(0).getTitle(), is(equalTo("highest price")));
    assertThat(outcome.get(1).getTitle(), is(equalTo("lowest price")));
    assertThat(outcome.get(2).getTitle(), is(equalTo("total volume")));
    assertThat(outcome.get(3).getTitle(), is(equalTo("closing price")));
    assertThat(outcome.get(0).getQuotes().size(), is(equalTo(0)));
    assertThat(outcome.get(1).getQuotes().size(), is(equalTo(0)));
    assertThat(outcome.get(2).getQuotes().size(), is(equalTo(0)));
    assertThat(outcome.get(3).getQuotes().size(), is(equalTo(0)));
  }

  @Test
  public void testMonthlyAggregateHappyPath() {
    spyQuoteService = spy(quoteService);
    // doReturn stubs method call for QuoteService helper method symbolToId(String symbol)
    doReturn((long) 1).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findMonthlyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Arrays.asList(max));
    when(quoteRepository.findMonthlyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Arrays.asList(min));
    when(quoteRepository.findMonthlyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(5900);;

    List<Aggregate> outcome = spyQuoteService.monthlyAggregate("TEST", "2001-03");

    assertThat(outcome.size(), is(equalTo(3)));
    assertThat(outcome.get(0).getTitle(), is(equalTo("highest price")));
    assertThat(outcome.get(1).getTitle(), is(equalTo("lowest price")));
    assertThat(outcome.get(2).getTitle(), is(equalTo("total volume")));
    assertThat(outcome.get(0).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(1).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(outcome.get(2).getQuotes().get(0).getSymbol(), is(equalTo((long) 1)));
    assertTrue(outcome.get(0).getQuotes().contains(max));
    assertTrue(outcome.get(1).getQuotes().contains(min));
    assertThat(outcome.get(0).getQuotes().get(0).getPrice(), is(equalTo( 200.0)));
    assertThat(outcome.get(1).getQuotes().get(0).getPrice(), is(equalTo( 100.0)));
    assertThat(outcome.get(2).getQuotes().get(0).getPrice(), is(equalTo( 0.0)));
    assertThat(outcome.get(0).getQuotes().get(0).getVolume(), is(equalTo( 1000)));
    assertThat(outcome.get(1).getQuotes().get(0).getVolume(), is(equalTo( 1000)));
    assertThat(outcome.get(2).getQuotes().get(0).getVolume(), is(equalTo( 5900)));
  }

  @Test
  public void testMonthlyAggregateBadPath() {
    spyQuoteService = spy(quoteService);
    // doThrow stubs method call for QuoteService helper method symbolToId(String symbol)
    doThrow(new BadRequestException()).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findMonthlyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findMonthlyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findMonthlyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(-1);

    exceptionGrabber.expect(BadRequestException.class);
    spyQuoteService.monthlyAggregate("BAD", "2001-03-01");
  }

  @Test
  public void testMonthlyAggregateNoData() {
    spyQuoteService = spy(quoteService);
    // doReturn stubs method call for QuoteService helper method symbolToId(String symbol)
    doReturn((long) -1).when(spyQuoteService).symbolToId(anyString());

    when(quoteRepository.findMonthlyMax(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findMonthlyMin(anyLong(), anyObject(), anyObject()))
        .thenReturn(Collections.emptyList());
    when(quoteRepository.findMonthlyVolume(anyLong(), anyObject(), anyObject()))
        .thenReturn(-1);

    List<Aggregate> outcome = spyQuoteService.monthlyAggregate("AAPL","2020-02-20");
    assertThat(outcome.size(), is(equalTo(3)));
    assertThat(outcome.get(0).getTitle(), is(equalTo("highest price")));
    assertThat(outcome.get(1).getTitle(), is(equalTo("lowest price")));
    assertThat(outcome.get(2).getTitle(), is(equalTo("total volume")));
    assertThat(outcome.get(0).getQuotes().size(), is(equalTo(0)));
    assertThat(outcome.get(1).getQuotes().size(), is(equalTo(0)));
    assertThat(outcome.get(2).getQuotes().size(), is(equalTo(0)));
  }
}
