package com.github.i49.bee.common.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.i49.bee.buzz.BuzzException;

public abstract class JsonValidator {

	private static final ValueType STRING_TYPE = new ValueType(JsonNodeType.STRING);
	private static final ValueType NUMBER_TYPE = new ValueType(JsonNodeType.NUMBER);
	private static final ValueType BOOLEAN_TYPE = new ValueType(JsonNodeType.BOOLEAN);
	
	public JsonValidator() {
	}
	
	public abstract void validate(JsonNode node) throws ValidatorException; 
	
	public void validate(JsonNode node, Type type) throws ValidatorException {
		validate("(root node)", node, type);
	}
	
	private void validate(String name, JsonNode node, Type type) throws ValidatorException {
		JsonNodeType nodeType = node.getNodeType();
		if (nodeType != type.getNodeType()) {
			throw new TypeMismatchException(name, type.getNodeType(), nodeType);
		}
		
		if (type instanceof ObjectType) {
			validateObject(node, (ObjectType)type);
		} else if (type instanceof ArrayType) {
			validateArray(name, node, (ArrayType)type);
		}
	}
	
	private void validateObject(JsonNode node, ObjectType type) throws ValidatorException {
		
		for (Property p: type.required) {
			if (!node.hasNonNull(p.getName())) {
				throw new MissingFieldException(p.getName());
			}
		}

		Iterator<Map.Entry<String, JsonNode>> it = node.fields();
		while (it.hasNext()) {
			Map.Entry<String, JsonNode> entry = it.next();
			String name = entry.getKey();
			if (!type.all.containsKey(name)) {
				throw new UnknownFieldException(name);
			}
			validate(name, entry.getValue(), type.getProperty(name).getType());
		}
	}
	
	private void validateArray(String name, JsonNode node, ArrayType type) throws ValidatorException {
		int index = 0;
		for (JsonNode item: node) {
			String itemName = name + "[" + index++ + "]";
			validate(itemName, item, type.getItemType()); 
		}
	}
	
	public static Property optional(String name, Type type) {
		return new Property(name, type, false);
	}
	
	public static Property required(String name, Type type) {
		return new Property(name, type, true);
	}
	
	public static ObjectType object(Property...properties) {
		return new ObjectType(properties);
	}
	
	public static ArrayType array(Type itemType) {
		return new ArrayType(itemType);
	}
	
	public static ValueType string() {
		return STRING_TYPE;
	}

	public static ValueType number() {
		return NUMBER_TYPE;
	}
	
	public static ValueType bool() {
		return BOOLEAN_TYPE;
	}
	
	public static abstract class Type {
		
		private final JsonNodeType nodeType;
		
		public Type(JsonNodeType nodeType) {
			this.nodeType = nodeType;
		}
		
		public JsonNodeType getNodeType() {
			return nodeType;
		}
	}
	
	public static class ValueType extends Type {
		public ValueType(JsonNodeType nodeType) {
			super(nodeType);
		}
	}

	public static class ObjectType extends Type {

		private final Map<String, Property> all = new HashMap<>();
		private final Set<Property> required = new HashSet<>();

		public ObjectType(Property[] properties) {
			super(JsonNodeType.OBJECT);
			for (Property p: properties) {
				this.all.put(p.getName(), p);
				if (p.isRequired()) {
					this.required.add(p);
				}
			}
		}
		
		public Property getProperty(String name) {
			return this.all.get(name);
		}
	}
	
	public static class ArrayType extends Type {
		
		private final Type itemType;
		
		public ArrayType(Type itemType) {
			super(JsonNodeType.ARRAY);
			this.itemType = itemType;
		}
		
		public Type getItemType() {
			return itemType;
		}
	}

	public static class Property {
		
		private final String name;
		private final Type type;
		private final boolean required;
		
		public Property(String name, Type type, boolean required) {
			this.name = name;
			this.type = type;
			this.required = required;
		}
		
		public String getName() {
			return name;
		}
		
		public Type getType() {
			return type;
		}
		
		public boolean isRequired() {
			return required;
		}
	}
}
