package com.example.arcan;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ArcanApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ArcanApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ArcanApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Initializing......");
	}
}
