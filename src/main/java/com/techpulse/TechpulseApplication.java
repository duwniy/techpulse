package com.techpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.techpulse.risk.RiskEngineService;

@SpringBootApplication
@EnableScheduling
public class TechpulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechpulseApplication.class, args);
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

