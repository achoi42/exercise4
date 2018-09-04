package com.solstice;

import com.solstice.controller.StockControllerIntegrationTest;
import com.solstice.controller.StockControllerUnitTest;
import com.solstice.repository.StockRepositoryIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(Suite.class)
@SuiteClasses({StockControllerIntegrationTest.class, StockControllerUnitTest.class,
    StockRepositoryIntegrationTest.class})
public class StockServiceApplicationTestSuite {
}
