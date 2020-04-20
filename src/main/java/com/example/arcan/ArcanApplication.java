package com.example.arcan;

import com.example.arcan.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.example.arcan.mapper")
@Import(SpringUtil.class)
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
