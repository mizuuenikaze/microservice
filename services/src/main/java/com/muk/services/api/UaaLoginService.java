package com.muk.services.api;

import java.util.Map;

import org.springframework.web.util.UriComponents;

public interface UaaLoginService {
	Map<String, Object> loginForClient(String username, String password, String clientId,
			UriComponents inUrlComponents);

	String approveClient(String approvalQuery, String cookie);

}
