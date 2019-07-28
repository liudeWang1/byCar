package com.maxwang.buycar.redis;

public class SuggestKey extends BasePrefix {

    public SuggestKey( String prefix) {
        super(prefix);
    }

    public static SuggestKey suggestKey=new SuggestKey("suggest");}
