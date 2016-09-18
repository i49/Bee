package com.github.i49.bee.web;

public enum Scheme {
	HTTP(80),
	HTTPS(443);
	
	private int defaultPort;
	
	private Scheme(int defaultPort) {
		this.defaultPort = defaultPort;
	}
	
	public int getDefaultPort() {
		return defaultPort;
	}
	
	public static Scheme of(String value) {
		if (value == null) {
			return null;
		}
		try {
			return valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
