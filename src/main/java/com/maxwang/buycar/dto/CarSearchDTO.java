package com.maxwang.buycar.dto;

import lombok.Data;

@Data
public class CarSearchDTO {

    private String structure;

    private String energy;

    private Integer carOil;

    private Integer carSpace;

    private Integer carHold;

    private double minPrice;

    private double maxPrice;

}
