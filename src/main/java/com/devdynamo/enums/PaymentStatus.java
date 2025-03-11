package com.devdynamo.enums;

public enum PaymentStatus {
    pending("Đang chờ"),
    completed("Đã hoàn thành"),
    failed("Không thành công"),
    refunded("Đã hoàn tiền");

    private final String status;
    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
