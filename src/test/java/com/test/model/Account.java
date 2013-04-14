package com.test.model;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private String accountId;
    private String userName;
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String homePhone;
    private String cellPhone;
    private Address homeAddress;
    
    private List anyInfo = new ArrayList();
    private List<CreditCard> creditCards = new ArrayList<CreditCard>();
    private List<Statement> statements = new ArrayList<Statement>();
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return homePhone;
	}
	/**
	 * @param homePhone the homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	/**
	 * @return the cellPhone
	 */
	public String getCellPhone() {
		return cellPhone;
	}
	/**
	 * @param cellPhone the cellPhone to set
	 */
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	/**
	 * @return the homeAddress
	 */
	public Address getHomeAddress() {
		return homeAddress;
	}
	/**
	 * @param homeAddress the homeAddress to set
	 */
	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;
	}
	/**
	 * @return the creditCards
	 */
	public List<CreditCard> getCreditCards() {
		return creditCards;
	}
	/**
	 * @param creditCards the creditCards to set
	 */
	public void setCreditCards(List<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}
	
	/**
	 * @return the anyInfo
	 */
	List getAnyInfo() {
		return anyInfo;
	}
	/**
	 * @param anyInfo the anyInfo to set
	 */
	void setAnyInfo(List anyInfo) {
		this.anyInfo = anyInfo;
	}
	
	/**
	 * @return the statements
	 */
	public List<Statement> getStatements() {
		return statements;
	}
	/**
	 * @param statements the statements to set
	 */
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
	
	public Statement getFirstStatement(){
		if (! statements.isEmpty()){
			return statements.get(0);
		}
		else {
			return null;
		}
	}
}
