package io.mkalugin.synergy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableCaching
@Slf4j
public class SynergyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynergyApplication.class, args);
	}

	@Bean
	public CommandLineRunner profileCheck(Environment environment) {
		return args -> log.info("Active Profiles: {}",
				String.join(", ", environment.getActiveProfiles()));
	}
}