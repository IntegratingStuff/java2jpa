package com.test.model;

import java.math.BigDecimal;
import java.util.Date;

public class Statement {

	private StatementId statementId;
	
	private BigDecimal amount;
	private Date date;
	
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the statementId
	 */
	public StatementId getStatementId() {
		return statementId;
	}
	/**
	 * @param statementId the statementId to set
	 */
	public void setStatementId(StatementId statementId) {
		this.statementId = statementId;
	}
	
}
