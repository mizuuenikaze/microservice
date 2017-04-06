package com.muk.web;

public interface WebConstants {
	interface Views {
		public static final String splash = "splash";
		public static final String offers = "offers";
		public static final String errorLog = "errorLog";
		public static final String spa = "spa";
		public static final String config = "config";
	}

	interface RedirectViews {
		public static final String offers = "redirect:/view/ext/offers";
		public static final String embeddedOffers = "redirect:/view/embed/offers";
	}

	interface Form {
		public static final String offerFormsContainer = "offerFormsContainer";
		public static final String pageSize = "pageSize";
		public static final String pageIndex = "pageIndex";
		public static final String currentProductFilter = "currentProductFilter";
		public static final String currentSegmentFilter = "currentSegmentFilter";
		public static final String deleteLink = "deleteLink";
		public static final String offerLink = "offerLink";
		public static final String total = "total";
	}

	interface Actions {
		public static final String offers = "/view/ext/offers";
		public static final String embeddedOffers = "/view/embed/offers";
		public static final String offerChange = "/view/ext/offerChange";
		public static final String embeddedOfferChange = "/view/embed/offerChange";
		public static final String errorLog = "/view/ext/errorLog";
		public static final String offerDelete = "/view/ext/delete";
		public static final String embeddedOfferDelete = "/view/embed/delete";
	}
}
