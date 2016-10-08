package com.github.i49.bee.hives.layouts;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CellEntry {

	private final String name;
	private final CellEntry parent;
	
	public CellEntry(String name, CellEntry parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public CellEntry getParent() {
		return parent;
	}
	
	public List<CellEntry> getAncestors() {
		LinkedList<CellEntry> result = new LinkedList<>();
		for (CellEntry entry = this; entry != null; entry = entry.getParent()) {
			result.addFirst(entry);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return getAncestors().stream().map(CellEntry::getName).collect(Collectors.joining("/"));
	}
}
