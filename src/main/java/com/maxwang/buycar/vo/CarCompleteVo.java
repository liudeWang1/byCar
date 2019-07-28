package com.maxwang.buycar.vo;

import lombok.Data;
import org.apache.ibatis.annotations.Param;

@Data
public class CarCompleteVo {

    private String name;

    private String firm;

    private String year;

    private String engine;

    private int horsepower;

    private String gearbox;

    private String torque;

    private String size;

    private String weight;

    private String structure;

    private String energy;

    private String emissions;

    private double oil;

    private double price;

    private String image;

    private int carId;

    private Integer carOil;

    private Integer carSpace;

    private Integer carHold;

}
