package com.test.model;

import java.io.Serializable;

public class StatementId implements Serializable{

	private static final long serialVersionUID = -5200312459525568233L;
	
	private String accountNumber;

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
}
