package com.github.i49.bee.core;

import java.util.function.Predicate;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkType;

public class LinkStrategy {

	private Predicate<Link> hyperlinkSelector;
	private Predicate<Link> externalResoureLinkSelector;
	
	public LinkStrategy() {
	}
	
	public Predicate<Link> getHyperlinkSelector() {
		return hyperlinkSelector;
	}
	
	public void setHyperlinkSelector(Predicate<Link> selector) {
		this.hyperlinkSelector = selector;
	}

	public Predicate<Link> getExternalResoureLinkSelector() {
		return externalResoureLinkSelector;
	}

	public void setExternalResoureLinkSelector(Predicate<Link> selector) {
		this.externalResoureLinkSelector = selector;
	}

	public static LinkStrategy createDefault() {
		LinkStrategy strategy = new LinkStrategy();
		strategy.setHyperlinkSelector(BASIC_HYPERLINK_SELECTOR);
		strategy.setExternalResoureLinkSelector(BASIC_EXTERNAL_RESOURCE_LINK_SELECTOR);
		return strategy;
	}
	
	public static final Predicate<Link> BASIC_HYPERLINK_SELECTOR = link->{
		return "a".equals(link.getElement());
	};
	
	public static final Predicate<Link> BASIC_EXTERNAL_RESOURCE_LINK_SELECTOR = link->{
		if (link.hasLinkType(LinkType.STYLESHEET)) {
			return true;
		}
		return ("img".equals(link.getElement()) || "script".equals(link.getElement()));
	};
}
