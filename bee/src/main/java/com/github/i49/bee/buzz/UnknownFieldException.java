package com.github.i49.bee.buzz;

public class UnknownFieldException extends BuzzException {

	private static final long serialVersionUID = 1L;
	private final String fieldName;
	
	public UnknownFieldException(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String getMessage() {
		return "JSON field \"" + getFieldName() + "\" is not supported.";
	}
}
