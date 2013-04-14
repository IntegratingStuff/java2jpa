package com.test.model;

public enum ShippingType {

    NEXT_DAY_OVERNIGHT("Next Day - Overnight"),
    EXPEDITED_2_3_BUSINESS_DAYS("Expedited - 2-3 Business Days"),
    STANDARD_GROUND_5_7_BUSINESS_DAYS("Standard Ground - 5-7 Business Days");
    
    private final String value;

    ShippingType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ShippingType fromValue(String v) {
        for (ShippingType c : ShippingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
