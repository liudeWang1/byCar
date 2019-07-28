package com.maxwang.buycar.service.search;

import lombok.Data;

@Data
public class CarSuggest {

    private String input;

    // 默认权重
    private int weight = 10;
}
