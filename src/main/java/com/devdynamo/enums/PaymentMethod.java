package com.devdynamo.enums;

public enum PaymentMethod {
    cash("Tiền mặt"),
    credit_card("Thẻ"),
    bank_transfer("Chuyển khoản"),
    momo("Momo");

    private final String method;
    PaymentMethod(String method){
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
