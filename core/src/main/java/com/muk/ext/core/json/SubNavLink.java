package com.muk.ext.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.ExtensionRoot;

@JsonInclude(Include.NON_NULL)
public class SubNavLink {
	private ExtensionRoot parentId;
	private String[] path;
	private String href;
	private String appId;
	private String windowTitle;

	public ExtensionRoot getParentId() {
		return parentId;
	}

	public void setParentId(ExtensionRoot parent) {
		this.parentId = parent;
	}

	public String[] getPath() {
		return path;
	}

	public void setPath(String[] path) {
		this.path = path;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}
}
