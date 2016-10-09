package com.github.i49.bee.buzz;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.i49.bee.common.json.JsonValidator;
import com.github.i49.bee.common.json.ValidatorException;

public class BuzzJsonValidator extends JsonValidator {

	private static ObjectType ROOT = object(
		required("trips", array(
				object(
					required("location", string()),
					optional("distance", number())
				)
			)),
		required("sites", array(
				object(
					required("host", string()),
					optional("port", number()),
					optional("includes", array(string())),
					optional("excludes", array(string()))
				)	
			)),
		required("hive", object())
	);
	
	@Override
	public void validate(JsonNode node) throws ValidatorException {
		validate(node, ROOT);
	}
}
