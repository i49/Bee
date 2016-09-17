package com.github.i49.bee.core;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Task {

	private final LinkedList<Task> subtasks = new LinkedList<>();
	private Visitor visitor;

	public Task() {
	}
	
	public void run() {
		if (runBeforeSubtasks()) {
			executeSubtasks();
			runAfterSubtasks();
		}
	}
	
	public void addSubtask(Task subtask) {
		subtask.visitor = this.visitor;
		this.subtasks.add(subtask);
	}
	
	public void addSubtasksFirst(Iterable<Task> subtasks) {
		int pos = 0;
		for (Task subtask : subtasks) {
			subtask.visitor = this.visitor;
			this.subtasks.add(pos, subtask);
		}
	}

	public Visitor getVisitor() {
		return visitor;
	}
	
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}

	protected boolean runBeforeSubtasks() {
		return true;
	}
	
	protected void executeSubtasks() {
		while (!this.subtasks.isEmpty()) {
			Task subtask = this.subtasks.removeFirst();
			subtask.run();
			runAfterEachSubtask(subtask);
		}
	}

	protected void runAfterSubtasks() {
	}
	
	protected void runAfterEachSubtask(Task subtask) {
	}

	protected void notifyEvent(Consumer<BeeEventHandler> listener) {
		getVisitor().notifyEvent(listener);
	}
}
