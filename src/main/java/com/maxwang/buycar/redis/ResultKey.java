package com.maxwang.buycar.redis;

public class ResultKey extends BasePrefix {

    public static final int RESULT_EXPIRE=100;

    public ResultKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static ResultKey resultKey=new ResultKey(RESULT_EXPIRE,"result");}
