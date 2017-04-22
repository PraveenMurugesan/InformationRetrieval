package com.ir.tennis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author giridar
 */
@SpringBootApplication
@PropertySources({ @PropertySource("classpath:config.properties"),
	@PropertySource("classpath:log4j.properties") })
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}