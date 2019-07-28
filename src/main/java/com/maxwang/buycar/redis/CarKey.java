package com.maxwang.buycar.redis;

public class CarKey extends BasePrefix{

    public static final int CAR_EXPIRE=100;

    public CarKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static CarKey carKey=new CarKey(CAR_EXPIRE,"car");
}
