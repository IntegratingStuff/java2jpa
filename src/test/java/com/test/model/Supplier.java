package com.test.model;

public class Supplier implements Describable{

    private String supplierId;
    private String supplierName;
    
	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}
	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}
	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Override
	public String getName() {
		return supplierName;
	}
	
}
