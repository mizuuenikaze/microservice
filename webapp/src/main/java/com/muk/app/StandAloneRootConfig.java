package com.muk.app;

import org.springframework.context.annotation.Configuration;

/**
 *
 * Used with {@link org.apache.camel.spring.javaconfig.Main} to remove
 * all need for xml configuration.  Followed by an embedded jetty setup,
 * the web app context should bring in all other config and beans just as if it
 * were deployed to tomcat.
 *
 */
@Configuration
public class StandAloneRootConfig {

}
