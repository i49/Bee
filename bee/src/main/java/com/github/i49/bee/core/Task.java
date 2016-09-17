package com.github.i49.bee.core;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Task {

	private final LinkedList<Task> subtasks = new LinkedList<>();
	private Visitor visitor;

	public Task() {
	}
	
	public void doTask() {
		if (doBeforeSubtasks()) {
			doSubtasks();
			doAfterSubtasks();
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

	protected boolean doBeforeSubtasks() {
		return true;
	}
	
	protected void doAfterSubtasks() {
	}
	
	protected void doAfterSubtask(Task subtask) {
	}
	
	protected void notifyEvent(Consumer<BeeEventHandler> listener) {
		getVisitor().notifyEvent(listener);
	}
	
	private void doSubtasks() {
		while (!this.subtasks.isEmpty()) {
			Task subtask = this.subtasks.removeFirst();
			subtask.doTask();
			doAfterSubtask(subtask);
		}
	}
}
