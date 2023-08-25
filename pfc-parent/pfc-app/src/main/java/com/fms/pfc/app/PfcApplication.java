package com.fms.pfc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = { "com.fms.*" })
@ComponentScan(basePackages = { "com.fms.*" })
@EnableJpaRepositories(basePackages = { "com.fms.*" })
@EnableTransactionManagement
public class PfcApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PfcApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PfcApplication.class);
	}

}
