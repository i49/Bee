package com.github.i49.bee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.i49.bee.configuration.BeeConfiguration;
import com.github.i49.bee.configuration.JsonBeeConfiguration;
import com.github.i49.bee.configuration.LoadableBeeConfiguration;
import com.github.i49.bee.core.Bee;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Log log = LogFactory.getLog(Application.class);

	private static final String DEFAULT_JSON_NAME = "buzz.json";
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<String> nonOptions = args.getNonOptionArgs();
		String jsonName = nonOptions.size() > 0 ? nonOptions.get(0) : DEFAULT_JSON_NAME;
		Bee bee = createBee(JsonBeeConfiguration.class, jsonName);
		bee.buzz();
	}
	
	private Bee createBee(Class<? extends BeeConfiguration> clazz, String jsonName) throws InstantiationException, IllegalAccessException {
		BeeConfiguration configuration = clazz.newInstance();
		if (configuration instanceof LoadableBeeConfiguration) {
			((LoadableBeeConfiguration)configuration).setSource(jsonName);
		}
		return configuration.getBee();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
