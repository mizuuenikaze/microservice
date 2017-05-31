package com.muk.ext.core.jackson;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PairDeserializer extends JsonDeserializer<Pair<String, Object>> {

	@Override
	public Pair<String, Object> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		final Object[] array = p.readValueAs(Object[].class);
		return Pair.of((String) array[0], array[1]);
	}
}
