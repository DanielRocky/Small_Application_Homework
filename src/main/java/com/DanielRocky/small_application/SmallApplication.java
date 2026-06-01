package com.DanielRocky.small_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * main entry point configuration class for the spring boot application
 * defines the application context and explicity activates spring data neo4j repository
 *
 * HOW TO START THIS APPLICATION
 * Start local neo4j database server instance
 * - Database Connection URL: bolt://localhost:7687
 * - Authentication Username: neo4j
 * - Authentication Password: our_password_v1
 * Run this application
 * Open web browser http://localhost:8081
 */
@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "com.DanielRocky.small_application.repository")
public class SmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmallApplication.class, args);
	}

}
