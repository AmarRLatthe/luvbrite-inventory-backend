package com.luvbrite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableCaching
@SpringBootApplication
@CrossOrigin
public class InventoryAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventoryAppApplication.class, args);
	}
}
