package com.solstice.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
public class QuoteRepositoryMonthlyQueryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private QuoteRepository quoteRepository;

  private static DateHelper dateHelper;
  private static SimpleDateFormat sdf;

  @BeforeClass
  public static void setUp() {
    dateHelper = new DateHelper();
    sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("CST"));
  }

  @Test
  public void testFindMonthlyMax() throws Exception {
    Date queryMonth = sdf.parse("2018-01");
    List<QuoteRecord> monthlyMax = quoteRepository.findMonthlyMax(1, queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertThat(monthlyMax.size(), is(equalTo(1)));
    assertThat(monthlyMax.get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(monthlyMax.get(0).getPrice(),is(equalTo(107.0)));
    assertThat(monthlyMax.get(0).getVolume(),is(equalTo(1070)));
  }

  @Test
  public void testFindMonthlyMaxNoResults() throws Exception {
    Date queryMonth = sdf.parse("2000-03");
    List<QuoteRecord> monthlyMax = quoteRepository.findMonthlyMax(1, queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertThat(monthlyMax.size(), is(equalTo(0)));
  }

  @Test
  public void testFindMonthlyMin() throws Exception {
    Date queryMonth = sdf.parse("2018-01");
    List<QuoteRecord> monthlyMin = quoteRepository.findMonthlyMin(1, queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertThat(monthlyMin.size(), is(equalTo(1)));
    assertThat(monthlyMin.get(0).getSymbol(), is(equalTo((long) 1)));
    assertThat(monthlyMin.get(0).getPrice(),is(equalTo(99.0)));
    assertThat(monthlyMin.get(0).getVolume(),is(equalTo(990)));
  }

  @Test
  public void testFindMonthlyMinNoResults() throws Exception {
    Date queryMonth = sdf.parse("2000-03");
    List<QuoteRecord> monthlyMin = quoteRepository.findMonthlyMin(1, queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertThat(monthlyMin.size(), is(equalTo(0)));
  }

  @Test
  public void testFindMonthlyVolume() throws Exception {
    Date queryMonth = sdf.parse("2018-01");
    Integer totalVolume = quoteRepository.findMonthlyVolume(1,queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertThat(totalVolume, is(equalTo(5150)));
  }
  @Test
  public void testFindMonthlyVolumeNoResults() throws Exception {
    Date queryMonth = sdf.parse("2000-03");
    Integer totalVolume = quoteRepository.findMonthlyVolume(1,queryMonth, dateHelper.getEndOfMonth(queryMonth));
    assertNull(totalVolume);
  }
}
