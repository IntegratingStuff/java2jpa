package com.test.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String orderId;
    private Account account;
    private CreditCard payment;
    private ShippingType shippingMethod;
    private StatusType status;
    private BigDecimal totalPrice;
    private List<LineItem> lineItems = new ArrayList<LineItem>();
	
    /**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	/**
	 * @return the payment
	 */
	public CreditCard getPayment() {
		return payment;
	}
	/**
	 * @param payment the payment to set
	 */
	public void setPayment(CreditCard payment) {
		this.payment = payment;
	}
	/**
	 * @return the shippingMethod
	 */
	public ShippingType getShippingMethod() {
		return shippingMethod;
	}
	/**
	 * @param shippingMethod the shippingMethod to set
	 */
	public void setShippingMethod(ShippingType shippingMethod) {
		this.shippingMethod = shippingMethod;
	}
	/**
	 * @return the status
	 */
	public StatusType getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusType status) {
		this.status = status;
	}
	/**
	 * @return the totalPrice
	 */
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the lineItems
	 */
	public List<LineItem> getLineItems() {
		return lineItems;
	}
	/**
	 * @param lineItems the lineItems to set
	 */
	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

}
