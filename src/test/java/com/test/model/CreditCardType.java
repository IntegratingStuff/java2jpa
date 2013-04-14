package com.test.model;

public enum CreditCardType {

    VISA("VISA"),
    MASTER_CARD("MasterCard"),
    AMEX("AMEX"),
    DISCOVER("Discover"),
    DINERS_CLUB("Diners Club");
    private final String value;

    CreditCardType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CreditCardType fromValue(String v) {
        for (CreditCardType c : CreditCardType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
