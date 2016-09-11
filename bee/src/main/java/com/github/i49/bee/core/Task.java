package com.github.i49.bee.core;

import java.util.LinkedList;

public class Task<T> {

	private Task<T> parent;
	private final LinkedList<Task<T>> subtasks = new LinkedList<>();
	private T context;

	public Task() {
	}
	
	public Task(T context) {
		this.context = context;
	}
	
	public void doTask() {
		if (doBeforeSubtasks()) {
			doSubtasks();
			doAfterSubtasks();
		}
	}
	
	public void addSubtask(Task<T> subtask) {
		subtask.parent = this;
		subtask.context = this.context;
		this.subtasks.add(subtask);
	}
	
	public Task<T> getParent() {
		return parent;
	}

	public T getContext() {
		return context;
	}

	protected boolean doBeforeSubtasks() {
		return true;
	}
	
	protected void doAfterSubtasks() {
	}
	
	protected void doAfterSubtask(Task<T> subtask) {
	}
	
	private void doSubtasks() {
		while (!this.subtasks.isEmpty()) {
			Task<T> subtask = this.subtasks.removeFirst();
			subtask.doTask();
			doAfterSubtask(subtask);
		}
	}
}
