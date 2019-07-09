package com.moonassist.system.scheduled;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.moonassist.bind.account.ExchangeEnum;
import com.moonassist.bind.order.Order;
import com.moonassist.bind.order.OrderStatus;
import com.moonassist.persistence.account.AccountDTO;
import com.moonassist.persistence.account.AccountRepository;
import com.moonassist.persistence.account.Exchange;
import com.moonassist.persistence.account.ExchangeDTO;
import com.moonassist.persistence.order.OrderDTO;
import com.moonassist.persistence.order.OrderRepository;
import com.moonassist.persistence.user.UserDTO;
import com.moonassist.service.AccountService;
import com.moonassist.service.TickerService;
import com.moonassist.service.UserService;
import com.moonassist.service.bean.RequestDetail;
import com.moonassist.service.exchange.connection.ExchangeService;
import com.moonassist.service.exchange.converters.OrderConverter;
import com.moonassist.service.exchange.binance.BinanceOrderService;
import com.moonassist.type.AccountId;
import com.moonassist.type.Id;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BinanceClosedOrdersTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(BinanceClosedOrdersTask.class);


  @Autowired
  private UserService userService;

  @Autowired
  private ExchangeService exchangeService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private BinanceOrderService binanceOrderService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private TickerService tickerService;


//  @Scheduled(cron = "0 */1 * * * ?")
  public void processAll() {

    LOGGER.info("Starting BinanceClosedOrdersTask");

    Map<String, TickerService.Ticker> tickers = tickerService.fetchTickers(ExchangeEnum.BINANCE);
    Preconditions.checkState(tickers != null &&  ! tickers.isEmpty(), "Unable to fetch tickers for Binance");

    for (UserDTO user : findActiveUsers()) {

      try {

        RequestDetail requestDetail = RequestDetail.builder()
          .userId(user.getId())
          .exchangeEnum(ExchangeEnum.BINANCE)
          .exchange(exchangeService.buildExchange(user.getId(), ExchangeEnum.BINANCE))
          .build();

        LOGGER.info("Processing Binance Closed orders for [" + user.getEmail() + "]");

        LOGGER.debug(String.format("UserId[%s] has exchange Binance", user.getId()));

        tickers.entrySet().forEach( entry -> {
          try {

            cacheOrders(requestDetail, entry.getKey(), user);
            sleep(500); // Do not overwhelm Binance API

          } catch (Exception e) {
            LOGGER.error("Error while attempting to cache orders for SymbolPair " + entry.getValue().getSymbol(), e);
          }
        });

      } catch (Exception e) {
        LOGGER.error(String.format("Failed to get Binance Orders for UserId[%s] email[%s] , message: %s", user.getId(), user.getEmail(), e.getMessage()), e);
      }
    }

    LOGGER.info("Finished processing BinanceClosedOrdersTask");
  }

  public List<UserDTO> findActiveUsers() {

    return userService.findActive().stream().
        filter( user -> accountService.hasExchange(user.getAccount().getId(), ExchangeEnum.BINANCE) )
        .collect(Collectors.toList());
  }

  @SneakyThrows
  @Transactional
  private void cacheOrders(final RequestDetail requestDetail, final String symbolPair, final UserDTO userDTO) {

    String replacedSymbolPair = symbolPair
        .replace("BCH", "BCC")
        .replace("YOYOW", "YOYO");

    List<Order> orders = binanceOrderService.findClosedOrders(requestDetail, replacedSymbolPair);

    orders.forEach( order -> {

      OrderDTO orderDTO = orderRepository.findByAccountAndExchangeAndExchangeOrderId(userDTO.getAccount(), Exchange.BINANCE, order.getExchangeOrderId());

      if ( orderDTO == null ) {
        orderDTO = OrderConverter.convert(order, userDTO.getAccount());
      }
      else {
        orderDTO.setSymbolPair(order.getSymbolPair());
        orderDTO.setStatus(OrderConverter.orderStatus(order.getStatus()));
        orderDTO.setPrice(order.getPrice());
        orderDTO.setAmount(order.getAmount());
        orderDTO.setUpdated(new Date());
      }

      orderRepository.saveAndFlush(orderDTO);
    });

  }

  @SneakyThrows
  private void sleep(long time) {
    Thread.sleep(time);
  }

}
