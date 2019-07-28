package com.maxwang.buycar.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static  String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static final String salt="123456";

    public static String inputPassToFormPass(String inputPass){
        String str=" "+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(4)+salt.charAt(5);
        return md5(str);
    }

    public static String formPassToDBPass(String inputPass,String salt){
        String str=" "+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(4)+salt.charAt(5);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass,String saltDB){
        String formPass=inputPassToFormPass(inputPass);
        return formPassToDBPass(formPass,saltDB);
    }

    public static void main(String[] args) {

        String formPass="a17e2846c7fec9f13d731b8efdbf0fca";

        System.out.println(inputPassToDBPass("123456", "123456"));
        System.out.println(formPassToDBPass(formPass, salt));

    }

}
