package com.maxwang.buycar.dto;

import lombok.Data;

@Data
public class CarSearch {

    private String keywords;

    private String structure;

    private String energy;

    private Integer carOil;

    private Integer carSpace;

    private Integer carHold;

    private double price;

}
