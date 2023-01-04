package com.cs.blackbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BlackboxApplication {


	public static void main(String[] args) {

		SpringApplication.run(BlackboxApplication.class, args);

	}

}
