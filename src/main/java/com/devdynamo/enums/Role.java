package com.devdynamo.enums;

public enum Role {
    admin("Quản trị viên"),
    customer("Người dùng");

    private final String name;
    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
