/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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
package com.muk.services.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.muk.ext.core.api.Dummy;

public interface OrderService {
	Dummy findById(String orderId) throws Exception;

	void updateOrder(Dummy order) throws Exception;

	void voidOrders(String orderId) throws Exception;

	/**
	 * 
	 * @param order
	 *            The order being processed
	 * @param uncollectedPayments
	 *            empty collection that is populated with uncollected payments
	 * @return The total paid from collected payments.
	 * @throws Exception
	 */
	BigDecimal gatherUncollectedPayments(Dummy order, List<Dummy> uncollectedPayments) throws Exception;

	/**
	 * 
	 * @param orderId
	 *            Order relating to the payment
	 * @param actionName
	 *            Action to take on payment
	 * @param amount
	 *            Amount to capture
	 * @param payment
	 *            Payment corresponding to transaction and credit card
	 * @param remainder
	 *            Amount to authorize after partial capture
	 * @return The remaining authorized amount
	 * @throws Exception
	 */
	Dummy newSplitPayment(String orderId, String actionName, Double amount, Dummy payment, BigDecimal remainder)
			throws Exception;

	void performPayment(String actionName, String orderId, String paymentId, Double amount) throws Exception;

	Dummy createGiftCardPackage(String orderId,
			Collection<Dummy> Dummys) throws Exception;

	void shipPackage(String orderId, String packageId) throws Exception;

	void addNote(String orderId, String message) throws Exception;

	void displayGiftCodes(String orderId, String attributeFqn, List<String> allCodes) throws Exception;

	void failDummy(String orderId, String message) throws Exception;
}
