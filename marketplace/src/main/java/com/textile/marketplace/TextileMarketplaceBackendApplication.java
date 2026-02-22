package com.textile.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TextileMarketplaceBackendApplication {

	public static void main(String[] args) {

        SpringApplication.run(TextileMarketplaceBackendApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 Textile B2B Marketplace Backend Started!");
        System.out.println("📍 Local: http://localhost:8080");
        System.out.println("📚 API Base: http://localhost:8080/api");
        System.out.println("==========================================");
	}

}
