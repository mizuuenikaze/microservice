package com.muk.services.api;

import java.util.List;

import com.muk.ext.core.ExtensionRoot;
import com.muk.ext.core.api.Dummy;

public interface AdminExtensionService {

	List<Dummy> listContainers() throws Exception;

	boolean addSubNavLink(ExtensionRoot parent, String href, String[] path, String title) throws Exception;

	boolean updateSubNavLink(ExtensionRoot parent, String href, String[] path, String title) throws Exception;

	boolean deleteSubNavLink() throws Exception;

}
