package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.Map;

public class InternalNode extends Node {

	private final Map<String, Node> entries = new HashMap<>();
	private int leafNodeCount;
	private int internalNodeCount;
	
	public InternalNode(String name) {
		super(name);
	}

	public int countLeafNodes() {
		return leafNodeCount;
	}
	
	public int countInternalNodes() {
		return internalNodeCount;
	}
	
	public Node findChild(String name) {
		return entries.get(name);
	}
	
	public void addChild(String name, LeafNode node) {
		entries.put(name, node);
		++leafNodeCount;
	}

	public void addChild(String name, InternalNode node) {
		entries.put(name, node);
		++internalNodeCount;
	}
}
