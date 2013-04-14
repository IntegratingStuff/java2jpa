package com.test.model;

public class CreditCard {

    private String creditCardId;
    private CreditCardType type;
    private String number;
    private String expiration;
    private Address billingAddress;
    
	/**
	 * @return the creditCardId
	 */
	public String getCreditCardId() {
		return creditCardId;
	}
	/**
	 * @param creditCardId the creditCardId to set
	 */
	public void setCreditCardId(String creditCardId) {
		this.creditCardId = creditCardId;
	}
	/**
	 * @return the type
	 */
	public CreditCardType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(CreditCardType type) {
		this.type = type;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the expiration
	 */
	public String getExpiration() {
		return expiration;
	}
	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	/**
	 * @return the billingAddress
	 */
	public Address getBillingAddress() {
		return billingAddress;
	}
	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

}
