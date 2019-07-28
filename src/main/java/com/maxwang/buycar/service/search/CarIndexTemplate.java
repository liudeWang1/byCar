package com.maxwang.buycar.service.search;

import lombok.Data;

import java.util.List;

/**
 * 索引结构模版
 */
@Data
public class CarIndexTemplate {

    private Integer carId;

    private String name;

    private String firm;

    private String structure;

    private String energy;

    private Double price;

    private List<String> tags;

    private String image;

    //完成匹配
    private List<CarSuggest> suggest;
}
