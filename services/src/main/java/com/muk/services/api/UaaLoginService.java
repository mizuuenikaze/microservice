package com.muk.services.api;

import java.util.Map;

import org.springframework.web.util.UriComponents;

/**
 *
 * Describes a simplified login server rest api.
 * <p>
 * This allows for a login api to Uaa that does not contain any ui. Great for SPA clients.
 */
public interface UaaLoginService {
	Map<String, Object> loginForClient(String username, String password, String clientId,
			UriComponents inUrlComponents);

	String approveClient(String approvalQuery, String cookie);

}
