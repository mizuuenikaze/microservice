package com.muk.ext.core.jackson;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class PairSerializer extends JsonSerializer<Pair> {

	@Override
	public void serialize(Pair value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartArray(2);
		gen.writeObject(value.getLeft());
		gen.writeObject(value.getRight());
		gen.writeEndArray();

	}

}
