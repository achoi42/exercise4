package com.solstice.repository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.solstice.domain.QuoteRecord;
import com.solstice.util.DateHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
@DatabaseSetup("classpath:test-dataset.xml")
public class QuoteRepositoryDailyQueryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private QuoteRepository quoteRepository;

  private static DateHelper dateHelper;
  private static SimpleDateFormat sdf;

  @BeforeClass
  public static void setUp() {
    dateHelper = new DateHelper();
    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("CST"));
  }

  @Test
  public void testFindDailyMax() throws Exception{
    Date queryDate = sdf.parse("2018-01-01");
    List<QuoteRecord> dailyMax = quoteRepository.findDailyMax(1, queryDate, dateHelper.getEnd(queryDate));
    assertThat(dailyMax.size(), is(equalTo(1)));
    assertThat(dailyMax.get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(dailyMax.get(0).getPrice(),is(equalTo(106.0)));
    assertThat(dailyMax.get(0).getVolume(),is(equalTo(1060)));
  }

  @Test
  public void testFindDailyMaxNoResults() throws Exception {
    Date queryDate = sdf.parse("1234-99-99");
    List<QuoteRecord> dailyMax = quoteRepository.findDailyMax(1, queryDate, dateHelper.getEnd(queryDate));
    assertThat(dailyMax.size(), is(equalTo(0)));
  }

  @Test
  public void testFindDailyMin() throws Exception{
    Date queryDate = sdf.parse("2018-01-01");
    List<QuoteRecord> dailyMin = quoteRepository.findDailyMin(1, queryDate, dateHelper.getEnd(queryDate));
    assertThat(dailyMin.size(), is(equalTo(1)));
    assertThat(dailyMin.get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(dailyMin.get(0).getPrice(),is(equalTo(100.0)));
    assertThat(dailyMin.get(0).getVolume(),is(equalTo(1000)));
  }

  @Test
  public void testFindDailyMinNoResults() throws Exception{
    Date queryDate = sdf.parse("1234-99-99");
    List<QuoteRecord> dailyMin = quoteRepository.findDailyMin(1, queryDate, dateHelper.getEnd(queryDate));
    assertThat(dailyMin.size(), is(equalTo(0)));
  }

  @Test
  public void testFindTotalVol() throws Exception {
    Date queryDate = sdf.parse("2018-01-01");
    Integer totalVolume = quoteRepository.findDailyVolume(1,queryDate, dateHelper.getEnd(queryDate));
    assertThat(totalVolume, is(equalTo(3090)));
  }

  @Test
  public void testFindTotalVolNoResults() throws Exception {
    Date queryDate = sdf.parse("1234-99-99");
    Integer totalVolume = quoteRepository.findDailyVolume(1,queryDate, dateHelper.getEnd(queryDate));
    assertNull(totalVolume);
  }

  @Test
  public void testClosingPrice() throws Exception {
    Date queryDate = sdf.parse("2018-01-01");
    List<QuoteRecord> closing = quoteRepository.findClosingPrice(1, queryDate, dateHelper.getEnd(queryDate));
    assertThat(closing.size(), is(equalTo(1)));
    assertThat(closing.get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(closing.get(0).getPrice(),is(equalTo(103.0)));
    assertThat(closing.get(0).getVolume(),is(equalTo(1030)));
  }

  @Test
  public void testFindClosingPriceNoResults() throws Exception {
    Date queryDate = sdf.parse("1234-99-99");
    List<QuoteRecord> closing = quoteRepository.findClosingPrice(1,queryDate, dateHelper.getEnd(queryDate));
    assertThat(closing.size(),is(equalTo(0)));
  }
}
