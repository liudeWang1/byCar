package com.maxwang.buycar.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
