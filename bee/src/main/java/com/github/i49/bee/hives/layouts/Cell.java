package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.Map;

public class Cell extends CellEntry {

	private final Map<String, CellEntry> entries = new HashMap<>();
	
	public Cell(String name, CellEntry parent) {
		super(name, parent);
	}
	
	public static Cell rootCell() {
		return new Cell("", null);
	}
	
	public CellEntry findChild(String name) {
		return this.entries.get(name);
	}
	
	public int countChildren() {
		return this.entries.size();
	}
	
	public CellEntry findDescendant(String path) {
		CellEntry found = null;
		Cell parent = this;
		for (String name: getNames(path)) {
			if (parent == null) {
				return null;
			}
			found = parent.findChild(name);
			if (found == null) {
				return null;
			} if (found instanceof Cell) {
				parent = (Cell)found;
			} else {
				parent = null;
			}
		}
		return found;
	}
	
	public Honey addHoney(String path) {
		int lastIndex = path.lastIndexOf('/');
		Cell parent = this;
		if (lastIndex >= 0) {
			parent = addCell(path.substring(0, lastIndex));
		}
		String name = path.substring(lastIndex + 1);
		CellEntry entry = parent.findChild(name);
		if (entry == null) {
			Honey honey = new Honey(name, parent);
			parent.entries.put(name, honey);
			return honey;
		} else if (entry instanceof Honey) {
			return (Honey)entry;
		}
		// Found entry is not Honey
		return null;
	}
	
	public Cell addCell(String path) {
		Cell parent = this;
		for (String name: getNames(path)) {
			CellEntry entry = parent.findChild(name);
			if (entry == null) {
				Cell cell = new Cell(name, parent); 
				parent.entries.put(name, cell);
				parent = cell;
			} else if (entry instanceof Cell) {
				parent = (Cell)entry;
			} else {
				// Found entry is not Cell
				return null;
			}
		}
		return parent;
	}

	private static String[] getNames(String path) {
		int begin = path.startsWith("/") ? 1 : 0;
		int end = path.length() - (path.endsWith("/") ? 1 : 0);
		return path.substring(begin, end).split("/");
	}
}
