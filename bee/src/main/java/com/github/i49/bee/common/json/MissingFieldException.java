package com.github.i49.bee.common.json;

public class MissingFieldException extends ValidatorException {

	private static final long serialVersionUID = 1L;
	private final String fieldName;
	
	public MissingFieldException(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	@Override
	public String getMessage() {
		return "Required JSON field \"" + getFieldName() + "\" is missing.";
	}
}
