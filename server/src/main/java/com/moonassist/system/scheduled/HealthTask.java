package com.moonassist.system.scheduled;

import com.moonassist.persistence.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HealthTask {

  @Autowired
  private UserRepository userRepository;

  @Scheduled(cron = "0/30 * * * * ?")
  public void health() {

    log.info("Task Manager Healthy");

  }

}