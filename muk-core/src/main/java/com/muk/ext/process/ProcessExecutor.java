/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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
package com.muk.ext.process;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

public class ProcessExecutor {

	public int runCommandLine(CommandLine cmdLine, long maxWaitTimeInMillis) throws IOException {
		ExecuteWatchdog processWatchDog = new ExecuteWatchdog(maxWaitTimeInMillis);
		Executor executor = new DefaultExecutor();

		executor.setExitValue(0);
		executor.setWatchdog(processWatchDog);

		int result = 1;
		result = executor.execute(cmdLine);

		return result;
	}
}
