package com.github.i49.bee.buzz;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.LoadableBeeConfiguration;

public class JsonBeeConfiguration implements LoadableBeeConfiguration {

	private File source;
	
	@Override
	public Bee getBee() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(this.source);
			Bee bee = new Bee();
			return bee;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setSource(String source) {
		this.source = new File(source);
	}
}
