package com.github.i49.bee.web;

public enum LinkType {
 	ALTERNATE, 
 	AUTHOR,
 	BOOKMARK,
 	DNS_PREFETCH,
 	EXTERNAL,
 	HELP,
 	ICON,
 	LICENSE,
 	NEXT,
 	NOFOLLOW,
 	NOREFERRER,
 	NOOPENER,
 	PINGBACK, 
 	PRECONNECT, 
 	PREFETCH, 
 	PRELOAD, 
 	PRERENDER,
 	PREV,
 	SEARCH, 
 	STYLESHEET,
 	TAG;
 	
	public static LinkType of(String text) {
		if (text == null) {
			return null;
		}
		try {
			return valueOf(text.replace("-", "_").toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
