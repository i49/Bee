package com.github.i49.bee.buzz;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class JsonValidator {

	public JsonValidator() {
	}
	
	public void validate(JsonNode node, Fields fields) throws BuzzException {
		fields.validate(node);
	}
	
	public void validateArray(JsonNode node, String name) throws BuzzException {
		if (!node.isArray()) {
			throw new TypeMismatchException(name, JsonNodeType.ARRAY, node.getNodeType());
		}
	}

	public static Fields fields(Field...fields) {
		return new Fields(fields);
	}
	
	public static Field optional(String name) {
		return field(name, false);
	}
	
	public static Field required(String name) {
		return field(name, true);
	}
	
	public static Field field(String name, boolean required) {
		return new Field(name, required);
	}

	public static class Fields {

		private final Set<String> all = new HashSet<>(); 
		private final Set<String> required = new HashSet<>();
		
		public Fields(Field[] fields) {
			for (Field f: fields) {
				all.add(f.name);
				if (f.required) {
					required.add(f.name);
				}
			}
		}

		public void validate(JsonNode node) throws BuzzException {
			Iterator<String> it = node.fieldNames();
			while (it.hasNext()) {
				String name = it.next();
				if (!all.contains(name)) {
					throw new UnknownFieldException(name);
				}
			}
			for (String name: this.required) {
				if (!node.hasNonNull(name)) {
					throw new MissingFieldException(name);
				}
			}
		}
	}
	
	public static class Field {
		
		private final String name;
		private final boolean required;
		
		public Field(String name, boolean required) {
			this.name = name;
			this.required = required;
		}
	}
}
