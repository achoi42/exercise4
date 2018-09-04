package com.solstice;

import com.solstice.controller.QuoteControllerDailyAggregateTest;
import com.solstice.controller.QuoteControllerLoadIntegrationTest;
import com.solstice.controller.QuoteControllerLoadUnitTest;
import com.solstice.controller.QuoteControllerMonthlyAggregateTest;
import com.solstice.repository.QuoteRepositoryDailyQueryIntegrationTest;
import com.solstice.repository.QuoteRepositoryMonthlyQueryIntegrationTest;
import com.solstice.service.QuoteServiceUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    QuoteControllerDailyAggregateTest.class,
    QuoteControllerMonthlyAggregateTest.class,
    QuoteControllerLoadIntegrationTest.class,
    QuoteControllerLoadUnitTest.class,
    QuoteRepositoryDailyQueryIntegrationTest.class,
    QuoteRepositoryMonthlyQueryIntegrationTest.class,
    QuoteServiceUnitTest.class
})
public class QuoteServiceApplicationTests {

  @Test
  public void contextLoads() {
  }

}
