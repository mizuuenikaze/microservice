package com.muk.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.api.Dummy;

@JsonInclude(Include.NON_NULL)
public class MozuSetting extends Dummy {
	private ApplicationInfo applicationInfo;

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}

	@JsonInclude(Include.NON_NULL)
	public static class ApplicationInfo {
		private String appName;
		private String version;

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}
}
