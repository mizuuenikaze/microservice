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
package com.muk.services.api.builder;

import org.springframework.web.client.RestOperations;

public interface RestTemplateBuilder extends RestOperations {
	interface Api {
		public static final String tenant = "{base}/api/platform/tenants";
		public static final String thirdPartyApplication = "{tenant}/api/commerce/settings/applications";
		public static final String notificationEvent = "{base}/api/event/pull";
		public static final String entityList = "{tenant}/api/platform/entitylists";
		public static final String entity = "{tenant}/api/platform/entitylists/{parentId}/entities";
		public static final String entityContainer = "{tenant}/api/platform/entitylists/{parentId}/entityContainers";
		public static final String authTicket = "{base}/api/platform/applications/authtickets";
		public static final String customerAccount = "{tenant}/api/commerce/customer/accounts";
		public static final String customerAccountAndLogin = "{tenant}/api/commerce/customer/accounts/Bulk";
		public static final String customerAccountContact = "{tenant}/api/commerce/customer/accounts/{parentId}/contacts";
		public static final String customerAttribute = "{tenant}/api/commerce/customer/accounts/{parentId}/attributes";
		public static final String attributeForCustomer = "{tenant}/api/commerce/customer/attributedefinition/attributes";
		public static final String productType = "{tenant}/api/commerce/catalog/admin/attributedefinition/producttypes";
		public static final String attributeForCatalog = "{tenant}/api/commerce/catalog/admin/attributedefinition/attributes";
		public static final String attributeForCatalogValue = "{tenant}/api/commerce/catalog/admin/attributedefinition/attributes/{parentId}/VocabularyValues";
		public static final String product = "{tenant}/api/commerce/catalog/admin/products";
		public static final String storefrontProduct = "{tenant}/api/commerce/catalog/storefront/products";
		public static final String productProperty = "{tenant}/api/commerce/catalog/admin/products/{parentId}/Properties";
		public static final String category = "{tenant}/api/commerce/catalog/admin/categories";
		public static final String documentList = "{tenant}/api/content/documentlists";
		public static final String document = "{tenant}/api/content/documentlists/{parentId}/documents";
		public static final String documentType = "{tenant}/api/content/documenttypes";
		public static final String documentContent = "{tenant}/api/content/documentlists/{parentId}/documents/{id}/content";
		public static final String order = "{tenant}/api/commerce/orders/";
		public static final String states = "{base}/api/platform/reference/countriesWithStates";
		public static final String publishSetItems = "{tenant}/api/content/publishsets/{parentId}/items";
		public static final String publishSet = "{tenant}/api/content/publishsets";
		public static final String documentPublish = "{tenant}/api/content/documentpublishing/active";
		public static final String productInCatalogs = "{tenant}/api/commerce/catalog/admin/products/{parentId}/ProductInCatalogs";
		public static final String settingsTaxableTerritories = "{tenant}/api/commerce/settings/general/taxableterritories";
		public static final String catalogPublish = "{tenant}/api/commerce/catalog/admin/publishing/publishdrafts";
		public static final String productVariations = "{tenant}/api/commerce/catalog/admin/products/{parentId}/variations";
		public static final String priceLists = "{tenant}/api/commerce/catalog/admin/pricelists";
		public static final String priceListEntries = "{tenant}/api/commerce/catalog/admin/pricelists/{parentId}/entries";
		public static final String priceListEntry = "{tenant}/api/commerce/catalog/admin/pricelists/{parentId}/entries/{id}/USD";
		public static final String productOption = "{tenant}/api/commerce/catalog/admin/products/{parentId}/Options";
		public static final String productExtra = "{tenant}/api/commerce/catalog/admin/products/{parentId}/Extras";
		public static final String fulfillPackage = "{tenant}/api/commerce/orders/{parentId}/packages";
		public static final String productSearch = "{tenant}/api/commerce/catalog/storefront/productsearch/search";
	}
}
