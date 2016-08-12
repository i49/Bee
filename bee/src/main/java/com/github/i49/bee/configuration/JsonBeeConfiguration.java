package com.github.i49.bee.configuration;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.i49.bee.core.Bee;

public class JsonBeeConfiguration implements LoadableBeeConfiguration {

	private File source;
	
	@Override
	public Bee getBee() {
		JsonNode node = loadJson(this.source);
		if (node == null)
			return null;
		Bee bee = new Bee();
		return bee;
	}

	@Override
	public void setSource(String source) {
		this.source = new File(source);
	}
	
	private JsonNode loadJson(File source) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(source);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
