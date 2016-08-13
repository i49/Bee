package com.github.i49.bee;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.i49.bee.buzz.BuzzParser;
import com.github.i49.bee.core.Bee;

@Configuration
public class BeeConfiguration {

	@Autowired
	private ApplicationArguments args;
	
	private static final String DEFAULT_JSON_NAME = "buzz.json";

	@Bean
	public File jsonFile() {
		List<String> nonOptions = args.getNonOptionArgs();
		String jsonName = nonOptions.size() > 0 ? nonOptions.get(0) : DEFAULT_JSON_NAME;
		return new File(jsonName);
	}
	
	@Bean
	public Bee bee(File jsonFile) {
		BuzzParser parser = new BuzzParser();
		return parser.parseBuzz(jsonFile);
	}
}
