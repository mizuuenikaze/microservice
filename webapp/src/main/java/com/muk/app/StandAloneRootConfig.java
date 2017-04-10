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
