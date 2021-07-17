package com.stan.springboottrading.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    PRODDUCT_NOT_EXSIT(10,"product does not exist"),
    PRODUCT_INVENTORY_ERROR(11,"inventory shortage"),
    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}