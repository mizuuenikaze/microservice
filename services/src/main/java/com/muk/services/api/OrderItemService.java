package com.muk.services.api;

import java.util.List;

import com.muk.ext.core.api.Dummy;

public interface OrderItemService {
	List<Dummy> findByOrder(String orderId) throws Exception;
}
