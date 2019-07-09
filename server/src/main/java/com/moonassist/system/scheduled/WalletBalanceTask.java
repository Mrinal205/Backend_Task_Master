package com.moonassist.system.scheduled;

import com.moonassist.bind.account.ExchangeEnum;
import com.moonassist.persistence.account.AccountDTO;
import com.moonassist.persistence.account.NetWorthDTO;
import com.moonassist.persistence.account.NetWorthRepository;
import com.moonassist.persistence.user.UserDTO;
import com.moonassist.service.AccountService;
import com.moonassist.service.UserService;
import com.moonassist.service.WalletService;
import com.moonassist.service.exchange.connection.ExchangeService;
import com.moonassist.type.AccountId;
import com.moonassist.type.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import lombok.SneakyThrows;

@Component
public class WalletBalanceTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalletBalanceTask.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private UserService userService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private ExchangeService exchangeService;

  @Autowired
  private NetWorthRepository accountBalanceRepository;

  @Autowired
  private AccountService accountService;

  @SneakyThrows
  @Scheduled(cron = "0 */5 * * * ?")
  @Transactional
  public void calculateAll() {

    LOGGER.info("Starting Networth Task");

    Set<UserDTO> users = userService.findActive();

    for (UserDTO user : users) {
      try {

        if ( ! hasExchange(user.getAccount().getId())) {
          continue;
        }

        BigDecimal worth = walletService.getNetWorth(user.getId());

        LOGGER.info(String.format("UserId[%s] Net Worth [%s] USD", user.getId(), worth));
        savePoint(user.getAccount(), worth);

      } catch (Exception e) {
        LOGGER.error(String.format("Failed to get NetWorth for UserId[%s] email[%s], message: %s", user.getId(), user.getEmail(), e.getMessage()), e);
      }
    }

    LOGGER.info("Finished Networth Task");
  }

  private void savePoint(final AccountDTO accountDTO, final BigDecimal worth) {

    if (worth.compareTo(BigDecimal.ZERO) > 0) {

      accountBalanceRepository.save(NetWorthDTO.builder()
          .id(new Id())
          .account(accountDTO)
          .value(worth)
          .currency("USD")
          .created(new Date())
          .build());
    }
  }

  private boolean hasExchange(final Id<AccountId> accountId) {

    return ExchangeEnum.ACTIVE.stream()
        .filter( exchangeEnum -> accountService.hasExchange(accountId, exchangeEnum) )
        .findAny()
        .isPresent();

  }

}
