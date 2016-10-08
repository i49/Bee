package com.github.i49.bee.hives.layouts;

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
	
	public boolean isRoot() {
		return (this.parent == null);
	}
	
	public String getPath() {
		if (isRoot()) {
			return getName();
		} else {
			return getParent().getPath() + "/" + getName();
		}
	}
	
	@Override
	public String toString() {
		return getPath();
	}
}
