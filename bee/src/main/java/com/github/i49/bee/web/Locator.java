package com.github.i49.bee.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Locator {

	private final URI uri;
	
	protected Locator(URI uri) {
		this.uri = uri;
	}
	
	public String getScheme() {
		return uri.getScheme();
	}
	
	public String getAuthority() {
		return uri.getAuthority();
	}
	
	public String getPath() {
		return uri.getPath();
	}
	
	public String getQuery() {
		return uri.getQuery();
	}
	
	public String getFragment() {
		return uri.getFragment();
	}
	
	public Locator getParent() {
		String path = uri.getPath();
		if (path.endsWith("/")) {
			return this;
		}
		int lastIndex = path.lastIndexOf('/');
		if (lastIndex >= 0) {
			path = path.substring(0, lastIndex + 1);
		} else {
			path = "../";
		}
		return of(uri.getScheme(), uri.getAuthority(), path, uri.getQuery(), uri.getFragment());
	}
	
	public Locator relativize(Locator other) {
		final String scheme = getScheme();
		if (scheme != null && scheme.equals(other.getScheme())) {
			return other;
		}
		final String authority = getAuthority();
		if (authority != null && authority.equals(other.getAuthority())) {
			return other;
		}
		Path basePath = Paths.get(getPath());
		Path targetPath = Paths.get(other.getPath());
		Path relativePath = basePath.relativize(targetPath);
		return pathOf(relativePath.toString().replaceAll("\\\\", "/"));
	}

	public URI toURI() {
		return uri;
	}
	
	@Override
	public String toString() {
		return uri.toString();
	}
	
	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Locator other = (Locator) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public static Locator of(URI uri) {
		return new Locator(uri);
	}
	
	public static Locator pathOf(String path) {
		try {
			return of(new URI(path));
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static Locator of(String scheme, String authority, String path, String query, String fragment) {
		try {
			return new Locator(new URI(scheme, authority, path, query, fragment));
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
