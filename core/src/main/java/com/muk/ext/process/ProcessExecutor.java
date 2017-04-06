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
