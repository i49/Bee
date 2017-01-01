package com.github.i49.bee.buzz;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.github.i49.bee.core.Bee;
import com.github.i49.bee.core.Trip;
import com.github.i49.bee.core.WebSite;
import com.github.i49.hibiscus.validation.JsonValidator;
import com.github.i49.hibiscus.validation.ValidationResult;

public class BuzzParser {
	
	private final JsonValidator validator = new BuzzJsonValidator();

	public Bee parseBuzz(File file) throws BuzzException {
		try {
			JsonObject root = readJson(file);
			if (root != null) {
				return configureBee(new Bee(), root);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new BuzzException(e);
		}
	}

	private JsonObject readJson(File source) throws IOException {
		try (Reader reader = new FileReader(source)) {
			ValidationResult result = validator.validate(reader);
			if (result.hasProblems()) {
				return null;
			} else {
				return (JsonObject)result.getValue();
			}
		}
	}
	
	private Bee configureBee(Bee bee, JsonObject rootObject) {
		JsonArray trips = rootObject.getJsonArray("trips");
		configureTrips(bee.getTrips(), trips);
		
		JsonArray sites = rootObject.getJsonArray("sites");
		configureSites(bee.getSites(), sites);

		return bee;
	}
	
	private void configureTrips(List<Trip> trips, JsonArray array) {
		for (JsonValue e : array) {
			JsonObject o = (JsonObject)e;
			String location = o.getString("location");
			int distance = o.getInt("distance", 0);
			Trip seed = new Trip(location, distance); 
			trips.add(seed);
		}
	}
	
	private void configureSites(List<WebSite> sites, JsonArray array) {
		for (JsonValue e : array) {
			JsonObject o = (JsonObject)e;
			String host = o.getString("host");
			int port = -1;
			if (o.containsKey("port") && !o.isNull("port")) {
				port = o.getInt("port");
			}
			WebSite site = new WebSite(host, port);
			configureSiteDirectories(site.getIncludes(), o.getJsonArray("includes"));
			configureSiteDirectories(site.getExcludes(), o.getJsonArray("excludes"));
			sites.add(site);
		}
	}
	
	private void configureSiteDirectories(List<String> directories, JsonArray array) {
		if (array != null) {
			for (JsonValue e : array) {
				JsonString s = (JsonString)e;
				String path = s.getString().trim();
				directories.add(path);
			}
		}
	}
}
