package com.github.i49.bee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Log log = LogFactory.getLog(Application.class);
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		log.info("run called");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
