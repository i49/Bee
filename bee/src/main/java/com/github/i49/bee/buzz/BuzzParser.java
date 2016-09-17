package com.github.i49.bee.buzz;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.Trip;
import com.github.i49.bee.core.WebSite;

public class BuzzParser {

	private static final Log log = LogFactory.getLog(BuzzParser.class);
	
	public Bee parseBuzz(File file) {
		JsonNode rootNode = loadJson(file);
		if (rootNode != null) {
			return configureBee(new Bee(), rootNode);
		} else {
			return null;
		}
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
	
	private Bee configureBee(Bee bee, JsonNode node) {
		Set<String> supported = fields("seeds", "sites", "hive");
		Set<String> required = fields("seeds", "sites", "hive");
		validateNode(node, supported, required);
		
		JsonNode seedsNode = node.get("seeds");
		if (seedsNode != null) {
			if (seedsNode.isArray()) {	
				configureSeeds(bee.getSeeds(), seedsNode);
			}
		}
		
		JsonNode sitesNode = node.get("sites");
		if (sitesNode != null) {
			if (sitesNode.isArray()) {
				configureSites(bee.getSites(), sitesNode);
			}
		}

		return bee;
	}
	
	private void configureSeeds(List<Trip> seeds, JsonNode nodes) {
		Set<String> supported = fields("location", "distance");
		Set<String> required = fields("location");
		for (JsonNode node : nodes) {
			validateNode(node, supported, required);
			String location = node.path("location").textValue();
			int distance = node.path("distance").intValue();
			Trip seed = new Trip(location, distance); 
			seeds.add(seed);
		}
	}
	
	private void configureSites(List<WebSite> sites, JsonNode nodes) {
		Set<String> supported = fields("host", "port", "includes", "excludes");
		Set<String> required = fields("host");
		for (JsonNode node : nodes) {
			validateNode(node, supported, required);
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
	
	private static Set<String> fields(String...names) {
		Set<String> set = new HashSet<>();
		for (String name : names) {
			set.add(name);
		}
		return set;
	}

	private void validateNode(JsonNode node, Set<String> supported, Set<String> required) {
		Iterator<String> it = node.fieldNames();
		while (it.hasNext()) {
			String name = it.next();
			if (!supported.contains(name)) {
				log.error("JSON field \"" + name + "\" is not supported.");
			}
		}
		if (required != null) {
			for (String name: required) {
				if (!node.hasNonNull(name)) {
					log.error("Required JSON field \"" + name +"\" is missing.");
				}
			}
		}
	}
}
