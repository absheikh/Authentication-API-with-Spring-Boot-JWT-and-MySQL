package com.example.rest_api_jwt.utils;

import lombok.Data;

public @Data class ApiResponse<T> {
    private Boolean success;
    private String message;
    private int code;
    private T data;

    public ApiResponse(Boolean success, String message, int code, T data) {
        super();
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }
}
