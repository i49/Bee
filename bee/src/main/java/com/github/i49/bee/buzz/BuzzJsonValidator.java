package com.github.i49.bee.buzz;

import com.github.i49.hibiscus.schema.Schema;
import com.github.i49.hibiscus.validation.BasicJsonValidator;

import static com.github.i49.hibiscus.schema.SchemaComponents.*;

public class BuzzJsonValidator extends BasicJsonValidator {

	private static final Schema schema = schema(
		object(
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
		)
	);
	
	public BuzzJsonValidator() {
		super(schema);
	}
}
