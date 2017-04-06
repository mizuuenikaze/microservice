package com.muk.services.api;

import java.util.List;

import com.muk.ext.core.api.Dummy;

public interface ProductAttributeHelper {
	Dummy getAttributeTemplate(String fqn) throws Exception;

	Dummy getAttributeDetailForVocabularyValue(String valueName,
			List<Dummy> vocabularyValues) throws Exception;

}
