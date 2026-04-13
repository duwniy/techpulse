package com.techpulse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.techpulse.risk.RiskEngineService;

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
