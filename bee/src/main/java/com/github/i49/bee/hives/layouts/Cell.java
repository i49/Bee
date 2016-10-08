package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.Map;

public class Cell extends CellEntry {

	private final Map<String, CellEntry> entries = new HashMap<>();
	private int honeyCount;
	private int cellCount;
	
	public Cell(String name, CellEntry parent) {
		super(name, parent);
	}
	
	public static Cell rootCell() {
		return new Cell("", null);
	}
	
	public int countHoneys() {
		return honeyCount;
	}
	
	public int countCells() {
		return cellCount;
	}

	public Honey addHoney(String path) throws NotCellException, NotHoneyException {
		int lastIndex = path.lastIndexOf('/');
		Cell parent = this;
		if (lastIndex >= 0) {
			parent = getCell(path.substring(0, lastIndex));
		}
		String name = path.substring(lastIndex + 1);
		CellEntry entry = parent.findEntry(name);
		if (entry == null) {
			return parent.addHoneyEntry(new Honey(name, parent));
		} else if (entry instanceof Honey) {
			return (Honey)entry;
		}
		// Found entry is not Honey
		throw new NotHoneyException(entry.getPath());
	}
	
	public Cell getCell(String path) throws NotCellException {
		Cell parent = this;
		for (String name: getNames(path)) {
			CellEntry entry = parent.findEntry(name);
			if (entry == null) {
				parent = parent.addCellEntry(new Cell(name, parent));
			} else if (entry instanceof Cell) {
				parent = (Cell)entry;
			} else {
				// Found entry is not Cell
				throw new NotCellException(entry.getPath());
			}
		}
		return parent;
	}

	private CellEntry findEntry(String name) {
		return this.entries.get(name);
	}
	
	private Honey addHoneyEntry(Honey honey) {
		addEntry(honey);
		++honeyCount;
		return honey;
	}
	
	private Cell addCellEntry(Cell cell) {
		addEntry(cell);
		++cellCount;
		return cell;
	}
	
	private void addEntry(CellEntry entry) {
		if (findEntry(entry.getName()) != null) {
			throw new IllegalStateException();
		}
		this.entries.put(entry.getName(), entry);
	}
	
	private static String[] getNames(String path) {
		if (path.isEmpty() || path.equals("/")) {
			return new String[0];
		}
		int begin = path.startsWith("/") ? 1 : 0;
		int end = path.endsWith("/") ? -1 : 0;
		return path.substring(begin, path.length() + end).split("/");
	}
}
