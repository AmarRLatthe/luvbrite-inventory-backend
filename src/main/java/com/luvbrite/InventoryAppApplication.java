package com.luvbrite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class InventoryAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryAppApplication.class, args);
	}

}
