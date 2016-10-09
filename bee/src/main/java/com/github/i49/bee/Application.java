package com.github.i49.bee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.BeeException;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Log log = LogFactory.getLog(Application.class);
	
	@Autowired
	private Bee bee;
	
	@Override
	public void run(ApplicationArguments args) {
		try {
			if (bee != null) {
				bee.launch();
			}
		} catch (BeeException e) {
			log.error(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
