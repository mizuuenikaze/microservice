package com.muk.ext.core;

import com.muk.ext.core.json.HateoasLink;

public abstract class AbstractRestBeanGenerator<T> extends AbstractBeanGenerator<T> {
	public abstract HateoasLink createHateoasLink();
}
