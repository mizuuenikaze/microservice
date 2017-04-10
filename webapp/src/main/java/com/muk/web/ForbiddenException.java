package com.muk.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.muk.ext.core.ProjectCoreVersion;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;
}
