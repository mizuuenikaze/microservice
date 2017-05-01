/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.spring.config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.muk.services.security.BearerTokenAuthenticationProvider;

@Configuration
@EnableWebSecurity
@PropertySources(value = { @PropertySource(value = "classpath:security.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:${CONF_BASE}/conf/muk/security.properties", ignoreResourceNotFound = true) })
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	@Inject
	Environment environment;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Don't clear credentials from the authorization.  This is our oauth access token
		auth.eraseCredentials(false);

		// Add custom provider for a bearer token
		auth.authenticationProvider(bearerTokenAuthenticationProvider());

		// In memory users for basic auth
		final InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> authBuilder = auth
				.inMemoryAuthentication();
		String decodedPrincipal = null;
		String[] principalParts = null;

		for (final String creds : environment.getProperty("app.principals").split(",")) {
			decodedPrincipal = new String(Base64.decodeBase64(creds));
			principalParts = decodedPrincipal.split(":");

			authBuilder.withUser(principalParts[0]).password(principalParts[1]).roles("USER");
		}
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/view/ext/**").authenticated().and().formLogin().and().httpBasic();

		http.authorizeRequests().antMatchers("/view/embed/**").permitAll().and().csrf().disable().headers().disable();

	}

	@Bean(name = "camelAuthenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean(name = "camelAccessDecisionManager")
	public AccessDecisionManager accessDecisionManager() {
		final List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<AccessDecisionVoter<? extends Object>>();
		voters.add(new RoleVoter());

		final AffirmativeBased decisionManager = new AffirmativeBased(voters);
		decisionManager.setAllowIfAllAbstainDecisions(true);

		return decisionManager;
	}

	@Bean(name = "autoLoginUserDetailService")
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}

	@Bean(name = "bearerTokenAuthenticationProvider")
	public AuthenticationProvider bearerTokenAuthenticationProvider() {
		final BearerTokenAuthenticationProvider provider = new BearerTokenAuthenticationProvider();

		return provider;
	}

	@Bean(name = "restUserPolicy")
	public AuthorizationPolicy restUserPolicy() throws Exception {
		final SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();
		policy.setId("user");
		policy.setAlwaysReauthenticate(true);
		policy.setUseThreadSecurityContext(false);
		policy.setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy("ROLE_USER"));
		policy.setAccessDecisionManager(accessDecisionManager());
		policy.setAuthenticationManager(authenticationManagerBean());

		return policy;
	}

}
