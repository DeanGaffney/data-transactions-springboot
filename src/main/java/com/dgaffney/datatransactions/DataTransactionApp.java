package com.dgaffney.datatransactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.dgaffney")
public class DataTransactionApp {

	public static void main(String[] args) {
		SpringApplication.run(DataTransactionApp.class, args);
	}
}
