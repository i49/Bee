package com.github.i49.bee.configuration;

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
import com.github.i49.bee.core.SeedPage;
import com.github.i49.bee.core.WebSite;

public class JsonBeeConfiguration implements LoadableBeeConfiguration {

	private static final Log log = LogFactory.getLog(JsonBeeConfiguration.class);
	
	private File source;
	
	@Override
	public Bee getBee() {
		JsonNode rootNode = loadJson(this.source);
		if (rootNode != null) {
			return configureBee(new Bee(), rootNode);
		} else {
			return null;
		}
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
	
	private void configureSeeds(List<SeedPage> seeds, JsonNode nodes) {
		Set<String> supported = fields("location", "distance");
		Set<String> required = fields("location");
		for (JsonNode node: nodes) {
			validateNode(node, supported, required);
			String location = node.path("location").textValue();
			int distance = node.path("distance").intValue();
			SeedPage seed = new SeedPage(location, distance); 
			seeds.add(seed);
		}
	}
	
	private void configureSites(List<WebSite> sites, JsonNode nodes) {
		Set<String> supported = fields("host", "port", "paths");
		Set<String> required = fields("host");
		for (JsonNode node: nodes) {
			validateNode(node, supported, required);
			String host = node.path("host").textValue();
			int port = -1;
			if (node.hasNonNull("port"))
				port = node.get("port").intValue();
			WebSite site = new WebSite(host, port);
			JsonNode pathsNode = node.path("paths");
			if (pathsNode.isArray()) {
				for (JsonNode pathNode: pathsNode) {
					String path = pathNode.textValue().trim();
					site.getPaths().add(path);
				}
			}
			sites.add(site);
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
