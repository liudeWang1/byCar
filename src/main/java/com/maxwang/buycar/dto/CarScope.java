package com.maxwang.buycar.dto;

import lombok.Data;

@Data
public class CarScope {

    private String name;

    private double scope;

    public CarScope(String element, double score) {
        this.name = element;
        this.scope = score;
    }
}
