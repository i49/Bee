package com.github.i49.bee.common.json;

public class UnknownFieldException extends ValidatorException {

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
