package com.maxwang.buycar.domain;

import lombok.Data;

@Data
public class User {

    private Integer id;

    private String username;

    private String password;

    private String salt;

    private String tel;

    private Integer role;
}
