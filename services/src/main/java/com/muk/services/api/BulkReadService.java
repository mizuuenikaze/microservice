package com.muk.services.api;

import java.util.Collection;

import org.springframework.util.MultiValueMap;

public interface BulkReadService<T, TWRAPPER> {

	TWRAPPER getAll(String apiPath, Integer startIndex, Integer pageSize, String sortBy, String filter,
			String responseFields, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER getAll(String apiPath, MultiValueMap<String, String> parameters, Class<TWRAPPER> responseType)
			throws Exception;

	Collection<T> getAll(String apiPath, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER findBy(String apiPath, String filter, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER getSubAll(String apiPath, String parentId, Integer startIndex, Integer pageSize, String sortBy,
			String filter, String responseFields, Class<TWRAPPER> responseType) throws Exception;

	Collection<T> getSubAll(String apiPath, String parentId, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER findSubBy(String apiPath, String parentId, String filter, Class<TWRAPPER> responseType) throws Exception;

}
