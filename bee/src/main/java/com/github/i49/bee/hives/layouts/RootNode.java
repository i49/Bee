package com.github.i49.bee.hives.layouts;

public class RootNode extends InternalNode {

	public RootNode() {
		super("");
	}
	
	public Node findNode(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		} else if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		Node node = this;
		for (String name : path.split("/")) {
			node = ((InternalNode)node).findChild(name);
			if (node == null) {
				break;
			}
		}
		return node;
	}
}
