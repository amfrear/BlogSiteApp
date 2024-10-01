package com.alex.cst323.blogsiteapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class BlogSiteAppApplication {

	// Initialize SLF4J Logger
	private static final Logger logger = LoggerFactory.getLogger(BlogSiteAppApplication.class);

	public static void main(String[] args) {
		logger.info("Starting BlogSiteAppApplication...");
		SpringApplication.run(BlogSiteAppApplication.class, args);
		logger.info("BlogSiteAppApplication started successfully.");
	}
}
