package com.muk.services.strategy.impl;

import java.util.Map;

public class PassThroughTranslationStrategy extends CsvTranslationStrategy<String> {

	@Override
	public String translate(Map<String, String> source) {
		return String.join(",", source.values());
	}
}
