package com.github.i49.bee.core;

import java.util.function.Predicate;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkType;

public class DefaultExternalResourceLinkPredicate implements Predicate<Link> {

	@Override
	public boolean test(Link link) {
		if (link.hasLinkType(LinkType.STYLESHEET)) {
			return true;
		}
		return ("img".equals(link.getElement()) || "script".equals(link.getElement()));
	}
}
