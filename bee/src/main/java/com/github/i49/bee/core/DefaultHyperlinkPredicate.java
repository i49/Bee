package com.github.i49.bee.core;

import java.util.function.Predicate;

import com.github.i49.bee.web.Link;

public class DefaultHyperlinkPredicate implements Predicate<Link> {

	@Override
	public boolean test(Link link) {
		return "a".equals(link.getElement());
	}
}
