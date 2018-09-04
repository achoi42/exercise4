package com.solstice.service;

import static java.lang.Math.min;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.solstice.domain.Aggregate;
import com.solstice.domain.QuoteRecord;
import com.solstice.domain.Stock;
import com.solstice.repository.QuoteRepository;
import com.solstice.util.BadRequestException;
import com.solstice.util.DateHelper;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuoteService {

  @Value("${quote.url}")
  private String quotesUrl;
  @Value("${stock.url}")
  private String stockService;

  private ObjectMapper objectMapper;
  private DateHelper dateHelper;
  private QuoteRepository quoteRepository;

  private EurekaClient discoveryClient;
  private RestTemplate restTemplate;
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  public QuoteService(ObjectMapper objectMapper, DateHelper dateHelper, QuoteRepository quoteRepository, EurekaClient discoveryClient) {
    this.objectMapper = objectMapper;
    this.dateHelper = dateHelper;
    this.quoteRepository = quoteRepository;

    this.discoveryClient = discoveryClient;
    this.restTemplate = new RestTemplate();
  }

  protected QuoteService() {

  }

  public List<QuoteRecord> loadJson() throws IOException {
    URL myUrl = new URL(quotesUrl);
    List<QuoteRecord> mappedQuotes = objectMapper.readValue(
        myUrl, new TypeReference<List<QuoteRecord>>() {}
    );
    if(mappedQuotes == null) {
      logger.warn("No data loaded from URL");
      throw new BadRequestException();
    }
    logger.info("objectMapper mapped " + mappedQuotes.size() + " quoteRecord objects from dataset URL");
    QuoteRecord checker;
    QuoteRecord queried;
    for(int i = 0; i < mappedQuotes.size(); i += 400) {
      checker = mappedQuotes.get(i);
      queried = quoteRepository.findByDateAndAndSymbol(checker.getSymbol(), checker.getDate());
      if(queried != null) {
        logger.info("QuoteRecord exists - symbol " + checker.getSymbol() + ", price " + checker.getPrice() + ", volume " + checker.getVolume() + ", date " + checker.getStringDate());
        continue;
      }
      List<QuoteRecord> sublist = mappedQuotes.subList(i, min(i+400, mappedQuotes.size()));
      quoteRepository.save(sublist);
      try {
        Thread.sleep(1000);
      } catch(InterruptedException e) {
        logger.error("Error waiting on load endpoint");
        e.printStackTrace();
      }
    }
    return mappedQuotes;
  }

  public void clearTable() {
    quoteRepository.deleteAllInBatch();
  }

  public List<QuoteRecord> listQuotes() {
    List<QuoteRecord> found = quoteRepository.findAll();
    logger.info("Found " + found.size() + " QuoteRecords in existing database");
    return found;
  }

  public List<Aggregate> dailyAggregate(String name, String strDate) {
    Date queryDate = dateHelper.stringToDate(strDate);
    Date endOfQueryDate = dateHelper.getEnd(queryDate);
    long symId = symbolToId(name);
    Integer totalVolume = quoteRepository.findDailyVolume(symId, queryDate, endOfQueryDate);
    if(totalVolume == null) {
      totalVolume = -1;
    }
    return aggregateData(
        quoteRepository.findDailyMax(symId, queryDate, endOfQueryDate),
        quoteRepository.findDailyMin(symId, queryDate, endOfQueryDate),
        totalVolume,
        quoteRepository.findClosingPrice(symId, queryDate, endOfQueryDate),
        symId,
        queryDate
    );
  }

  public List<Aggregate> monthlyAggregate(String name, String strMonth) {
    Date queryMonth = dateHelper.stringToMonthStart(strMonth);
    Date endOfQueryMonth = dateHelper.getEndOfMonth(queryMonth);
    long symId = symbolToId(name);
    Integer totalVolume = quoteRepository.findMonthlyVolume(symId, queryMonth, endOfQueryMonth);
    if(totalVolume == null) {
      totalVolume = -1;
    }
    return aggregateData(
        quoteRepository.findMonthlyMax(symId, queryMonth, endOfQueryMonth),
        quoteRepository.findMonthlyMin(symId, queryMonth, endOfQueryMonth),
        totalVolume,
        null,
        symId,
        queryMonth
    );
  }

  private List<Aggregate> aggregateData(List<QuoteRecord> max, List<QuoteRecord> min
      , int totalVolume, List<QuoteRecord> closing
      , long symbolId, Date queryDate) {
    Aggregate myMax = new Aggregate("highest price", max);
    Aggregate myMin = new Aggregate("lowest price", min);
    Aggregate myTotalVolume;
    QuoteRecord volumeQuote;
    // No data returned for query date, default for totalVolume is -1
    if(totalVolume == -1) {
      volumeQuote = null;
    }
    else {
      volumeQuote = new QuoteRecord(symbolId, 0.0, totalVolume, "");
    }

    if(volumeQuote == null) {
      myTotalVolume = new Aggregate("total volume", Collections.emptyList());
    }
    else {
      myTotalVolume = new Aggregate("total volume", Arrays.asList(volumeQuote));
    }

    List<Aggregate> aggregates;
    if(closing != null) {
      Aggregate myClosing = new Aggregate("closing price", closing);
      aggregates = Arrays.asList(myMax, myMin, myTotalVolume, myClosing);
    }
    else {
      aggregates = Arrays.asList(myMax, myMin, myTotalVolume);
    }
    return aggregates;
  }

  public long symbolToId(String symbol) {
    Stock stock = restTemplate.getForObject(fetchGatewayUrl() + "stock/" + symbol + "/", Stock.class);
    if(stock == null) {
      logger.error("Error calling stock service for symbol " + symbol);
      throw new BadRequestException();
    }
    return stock.getSymbolId();
  }

  private String fetchGatewayUrl() {
    InstanceInfo instance = discoveryClient.getNextServerFromEureka("gateway-application", false);
    logger.debug("instanceID: {}", instance.getId());

    String stockServiceUrl = instance.getHomePageUrl();
    logger.debug("stock service homePageUrl: {}", stockServiceUrl);

    return stockServiceUrl;
  }
}
