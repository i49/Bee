package com.github.i49.bee.buzz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.Trip;
import com.github.i49.bee.core.WebSite;

import static com.github.i49.bee.buzz.JsonValidator.*;

public class BuzzParser {

	private static final Fields ROOT_FIELDS = fields(
		required("trips"), required("sites"), required("hive")
	);
	
	private static final Fields TRIP_FIELDS = fields(
		required("location"), optional("distance")
	);
	
	private static final Fields SITE_FIELDS = fields(
		required("host"), optional("port"), optional("includes"), optional("excludes")
	);

	private final JsonValidator v = new JsonValidator();
	
	public Bee parseBuzz(File file) throws BuzzException {
		JsonNode rootNode = loadJson(file);
		if (rootNode != null) {
			return configureBee(new Bee(), rootNode);
		} else {
			return null;
		}
	}

	private JsonNode loadJson(File source) throws BuzzException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(source);
		} catch (Exception e) {
			throw new BuzzException(e);
		}
	}
	
	private Bee configureBee(Bee bee, JsonNode node) throws BuzzException {

		v.validate(node, ROOT_FIELDS);
		
		JsonNode tripsNode = node.get("trips");
		v.validateArray(tripsNode, "trips");
		configureTrips(bee.getTrips(), tripsNode);
		
		JsonNode sitesNode = node.get("sites");
		v.validateArray(sitesNode, "sites");
		configureSites(bee.getSites(), sitesNode);

		return bee;
	}
	
	private void configureTrips(List<Trip> trips, JsonNode nodes) throws BuzzException {
		for (JsonNode node : nodes) {
			v.validate(node, TRIP_FIELDS);
			String location = node.path("location").textValue();
			int distance = node.path("distance").asInt(0);
			Trip seed = new Trip(location, distance); 
			trips.add(seed);
		}
	}
	
	private void configureSites(List<WebSite> sites, JsonNode nodes) throws BuzzException {
		for (JsonNode node : nodes) {
			v.validate(node, SITE_FIELDS);
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
