package com.devdynamo.enums;

public enum OrderStatus {
    pending("Đang chờ"),
    processing("Đang xử lý"),
    shipped("Đã vận chuyển"),
    delivere("Đã giao"),
    cancelled("Đã hủy");

    private final String status;
    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
