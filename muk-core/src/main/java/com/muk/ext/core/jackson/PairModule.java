package com.muk.ext.core.jackson;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.muk.ext.core.ProjectCoreVersion;

public class PairModule extends SimpleModule {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	public PairModule() {
		super();
		addSerializer(Pair.class, new PairSerializer());
		addDeserializer(Pair.class, new PairDeserializer());
	}
}
