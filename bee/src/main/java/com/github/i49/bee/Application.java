package com.github.i49.bee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.i49.bee.buzz.Buzz;
import com.github.i49.bee.buzz.BuzzLoader;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Log log = LogFactory.getLog(Application.class);

	private static final String DEFAULT_JSON_NAME = "buzz.json";
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<String> nonOptions = args.getNonOptionArgs();
		String jsonName = nonOptions.size() > 0 ? nonOptions.get(0) : DEFAULT_JSON_NAME;
		Buzz buzz = new BuzzLoader().load(jsonName);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
