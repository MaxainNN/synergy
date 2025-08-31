package io.mkalugin.synergy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class SynergyApplication {
	public static void main(String[] args) {
		SpringApplication.run(SynergyApplication.class, args);
	}
}