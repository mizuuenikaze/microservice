package com.muk.app;

import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.spring.Main;

public class StandAloneEntry {
	private Main main;

	public static void main (String[] args) throws Exception {
		final StandAloneEntry entry = new StandAloneEntry();
		entry.boot();
	}

	public void boot() throws Exception {
		main = new Main();

		// orchestrate stuff
		main.addMainListener(new Events());
		main.run();
	}

	public static class Events extends MainListenerSupport {
		@Override
		public void afterStart(MainSupport main) {
			System.out.println("JVM started.");
		}

		@Override
		public void beforeStop(MainSupport main) {
			System.out.println("JVM stopping.");
		}
	}
}
