package com.maxwang.buycar.redis;

public class UserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE=1000;

    public UserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static UserKey token=new UserKey(TOKEN_EXPIRE,"tk");
}
