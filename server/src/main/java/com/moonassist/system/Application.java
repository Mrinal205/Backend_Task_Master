package com.moonassist.system;

import com.moonassist.db.JPAConfiguration;
import com.moonassist.system.config.PoolingConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableWebSecurity
@Import({JPAConfiguration.class, PoolingConfig.class})
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.moonassist"})
public class Application {

	public static void main(String[] args) {

    SpringApplication.run(Application.class, args);
	}

}