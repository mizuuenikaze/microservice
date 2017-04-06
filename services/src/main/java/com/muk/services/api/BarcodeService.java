package com.muk.services.api;

import java.io.File;

public interface BarcodeService {
	File generateBarcode(File path, String giftCardId);
}
