package com.github.i49.bee.buzz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.Trip;
import com.github.i49.bee.core.WebSite;

public class BuzzParser {

	public Bee parseBuzz(File file) throws BuzzException {
		try {
			JsonNode rootNode = loadJson(file);
			new BuzzJsonValidator().validate(rootNode);
			if (rootNode != null) {
				return configureBee(new Bee(), rootNode);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new BuzzException(e);
		}
	}

	private JsonNode loadJson(File source) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(source);
	}
	
	private Bee configureBee(Bee bee, JsonNode node) {
		JsonNode tripsNode = node.get("trips");
		configureTrips(bee.getTrips(), tripsNode);
		
		JsonNode sitesNode = node.get("sites");
		configureSites(bee.getSites(), sitesNode);

		return bee;
	}
	
	private void configureTrips(List<Trip> trips, JsonNode nodes) {
		for (JsonNode node : nodes) {
			String location = node.path("location").textValue();
			int distance = node.path("distance").asInt(0);
			Trip seed = new Trip(location, distance); 
			trips.add(seed);
		}
	}
	
	private void configureSites(List<WebSite> sites, JsonNode nodes) {
		for (JsonNode node : nodes) {
			String host = node.path("host").textValue();
			int port = -1;
			if (node.hasNonNull("port")) {
				port = node.get("port").intValue();
			}
			WebSite site = new WebSite(host, port);
			configureSiteDirectories(site.getIncludes(), node.get("includes"));
			configureSiteDirectories(site.getExcludes(), node.get("excludes"));
			sites.add(site);
		}
	}
	
	private void configureSiteDirectories(List<String> directories, JsonNode nodes) {
		if (nodes != null && nodes.isArray()) {
			for (JsonNode node : nodes) {
				String path = node.textValue().trim();
				directories.add(path);
			}
		}
	}
}
