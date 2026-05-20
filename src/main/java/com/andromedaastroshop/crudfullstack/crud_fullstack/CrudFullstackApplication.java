package com.andromedaastroshop.crudfullstack.crud_fullstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CrudFullstackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudFullstackApplication.class, args);
	}

}
