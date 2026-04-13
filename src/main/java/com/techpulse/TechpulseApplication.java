package com.techpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.techpulse.risk.RiskEngineService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class TechpulseApplication {

	static {
		loadEnv();
	}

	public static void main(String[] args) {
		SpringApplication.run(TechpulseApplication.class, args);
	}

	private static void loadEnv() {
		try {
			if (Files.exists(Paths.get(".env"))) {
				List<String> lines = Files.readAllLines(Paths.get(".env"));
				for (String line : lines) {
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#")) continue;
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						String key = parts[0].trim();
						String value = parts[1].trim();
						System.setProperty(key, value);
					}
				}
				System.out.println("--- Loaded properties from .env successfully ---");
			} else {
				System.out.println("--- No .env file found, skipping manual load ---");
			}
		} catch (IOException e) {
			System.err.println("Failed to load .env file: " + e.getMessage());
		}
		
		// Debug logging for properties (sensitized)
		System.out.println("--- Active Datasource Configuration ---");
		System.out.println("DataSource URL: " + maskUrl(getProperty("SPRING_DATASOURCE_URL", "DATABASE_URL", "SUPABASE_DB_URL", "jdbc:postgresql://localhost:5432/postgres")));
		System.out.println("DataSource User: " + getProperty("SPRING_DATASOURCE_USERNAME", "DATABASE_USER", "SUPABASE_DB_USER", "postgres"));
		System.out.println("--- Current Environment Variables ---");
		System.out.println("SUPABASE_DB_URL present: " + (System.getenv("SUPABASE_DB_URL") != null));
		System.out.println("DATABASE_URL present: " + (System.getenv("DATABASE_URL") != null));
		System.out.println("SPRING_DATASOURCE_URL present: " + (System.getenv("SPRING_DATASOURCE_URL") != null));
	}

	private static String getProperty(String... keys) {
		for (String key : keys) {
			String val = System.getProperty(key);
			if (val == null) val = System.getenv(key);
			if (val != null) return val;
		}
		return "DEFAULT/NULL";
	}

	private static String maskUrl(String url) {
		if (url == null || url.length() < 10) return url;
		return url.replaceAll(":.*@", ":****@"); // Mask password if present in URL
	}

	@Bean
	public CommandLineRunner initRisk(RiskEngineService riskEngineService) {
		return args -> {
			System.out.println("--- Triggering Initial Risk Calculation ---");
			riskEngineService.recalculateAllRisks();
			System.out.println("--- Initial Risk Calculation Complete ---");
		};
	}

}
