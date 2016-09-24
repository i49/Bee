package com.github.i49.bee.core;

import java.util.function.Predicate;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkType;

public class LinkSelectors {

	public static final Predicate<Link> BASIC_HYPERLINK_SELECTOR = link->{
		return "a".equals(link.getElement());
	};
	
	public static final Predicate<Link> BASIC_EXTERNAL_RESOURCE_LINK_SELECTOR = link->{
		if (link.hasLinkType(LinkType.STYLESHEET)) {
			return true;
		}
		return ("img".equals(link.getElement()) || "script".equals(link.getElement()));
	};
	
	private LinkSelectors() {
	}
}
