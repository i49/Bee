package com.github.i49.bee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.i49.bee.core.Bee;

@SpringBootApplication
public class Application implements ApplicationRunner {

	@Autowired
	private Bee bee;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		bee.launch();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
